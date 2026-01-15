declare var saveAs: any;

import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import {
  CognitecService,
  CompareDataHolderService,
  GesService,
  ImageService,
  IncidentService,
  LoginService,
  PersonService,
  ReportService,
  Utils,
  SearchRequestService
} from '../shared/service/index';

import { Message, SelectItem } from 'primeng/primeng';
import { IdentificationBinningRequest } from '../shared/model/cognitec/index';
import { PersonResponse, PersonsRequest } from '../shared/model/person/index';
import { SearchReportRequest } from '../shared/model/report/search-report-request';
import { SearchBulkReportRequest } from '../shared/model/report/search-bulk-report-request';
import { Match } from '../shared/model/cognitec/identificationBinning-response.model';
import { ImageType } from '../shared/model/cognitec/imageType.enum';
import { SearchRequestResponse } from '../shared/model/search-request/search-request.response';
import {
  GENDER_D,
  GENDER_EMPTY,
  GENDER_M,
  GENDER_U,
  GENDER_W,
  GENDER_X,
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  NATIONALITY_EMPTY,
  ROLE_ADMIN,
  ROLE_SEARCHER
} from '../shared/constants';

import { DatePipe } from '@angular/common';
import { ImageTransformerComponent } from '../shared/image-transformer/image-transformer.component';
import { GesAnalyseResponse } from '../shared/model/ges/ges-analyse-response.model';

@Component({
  moduleId: module.id,
  templateUrl: 'searcher.component.html',
  providers: [DatePipe]
})
export class SearcherComponent implements OnInit, AfterViewInit {
  @ViewChild('imageTransformer') imageTransformer: ImageTransformerComponent;

  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  busy: boolean = false;
  selectedCaseId: Match;
  relatedCases: SelectItem[];
  infoTableHeaders = ['PKZ', 'Aktenzeichen', 'Antragstyp', 'Familienname', 'Vorname', 'Geburtsdatum', 'Geburtsort',
    'Staatsangehörigkeit', 'Geschlecht', 'AZR-Nummer', 'D-Nummer', 'Aufnahmedatum (MARiS-Bild)'];
  infoTableValues = ['', '', '', '', '', '', '', '', '', '', '', ''];
  exportEnabled = false;
  exportFullName = false;
  searchRestricted = false;
  currentPKZ: number = -1;
  relatedPersonsInfo: PersonResponse[];
  caseIdList: number[] = [];
  scoreList: number[] = [];
  MIN_CANDIDATES = 1;
  MAX_CANDIDATES = 50;
  maxCandidates: number = 50;
  MIN_AGE = 1;
  MAX_AGE = 99;
  minAge: number;
  MIN_SCORE = 1;
  MAX_SCORE = 99;
  minScore: number;
  gender: SelectItem[];
  selectedGender: string = GENDER_EMPTY;
  nationalities: SelectItem[];
  selectedNationality: string = NATIONALITY_EMPTY;
  note: string = '';

  searchRequestId: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private loginService: LoginService,
    private cognitecService: CognitecService,
    private gesService: GesService,
    private imageService: ImageService,
    private reportService: ReportService,
    private personService: PersonService,
    private datePipe: DatePipe,
    private incidentService: IncidentService,
    private compareDataHolderService: CompareDataHolderService,
    private searchRequestService: SearchRequestService
  ) {
  }

  ngOnInit() {
    this.relatedCases = [];
    this.relatedPersonsInfo = [];
    this.caseIdList = [];
    this.scoreList = [];
    this.minScore = this.MIN_SCORE;
    this.maxCandidates = this.MAX_CANDIDATES;

    if (!this.loginService.isAuthenticated() || ! this.loginService.isAuthorized([ROLE_SEARCHER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.fillGender();
      this.fillNationalities();

      this.route.queryParams.subscribe(params => {
        if (params['requestId']) {
          this.searchRequestId = params['requestId'];
          setTimeout(() => {
            this.loadPreviousSearch();
          }, 200);
        }
      });
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (this.imageTransformer) {
        this.imageTransformer.setAnnotateCnvReq(this.annotateReq.bind(this));
        this.imageTransformer.setPrimaryActionReq(this.searchDatabase.bind(this));
        this.imageTransformer.setSecondaryActionReq(this.gesVerifyModified.bind(this));
      }
    }, 100);
  }

  private searchDatabase(): void {
    const img = this.imageTransformer.getModifiedImage(false);
    if (!img) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.NoImageLoaded');
      return;
    }
    this.identificationBinning(img, ImageType.CANVAS_IMAGE);
  }

  private gesVerifyModified(): void {
    const img1 = this.imageTransformer.getModifiedImage(true);
    const img2 = this.imageTransformer.getModifiedImage(false);

    if (!img1 || !img2) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ImagesNotLoaded');
      return;
    }

    this.busy = true;

    this.gesService.compareEmbeddings(img1, img2).subscribe(
      resp => {
        this.busy = false;
        if (resp && resp.score !== undefined) {
          var score = parseFloat(this.utils.floorFigure(resp.score, 2));
          this.imageTransformer.setScore(score, resp.score);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GesCompareFailed');
      }
    );
  }

  loadPreviousSearch() {
    if (! this.searchRequestId) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Zadaj Request ID');
      return;
    }

    this.busy = true;
    const requestId = parseInt(this.searchRequestId, 10);

    this.searchRequestService.getById(requestId).subscribe(
      (resp: SearchRequestResponse) => {
        this.busy = false;

        if (!resp || !resp.requestId) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Request not found');
          return;
        }

        if (resp.bilddaten) {
          const base64Raw = (typeof resp.bilddaten === 'string') ? resp.bilddaten : this.arrayBufferToBase64(resp.bilddaten);
          const dataUri = 'data:image/png;base64,' + base64Raw;
          this.imageTransformer.loadImage(false, dataUri);
        } else {
          this.imageTransformer.resetImage(false);
        }

        this.maxCandidates = resp.maxCandidates || this.MAX_CANDIDATES;
        this.minScore = resp.minScore || this.MIN_SCORE;
        this.selectedGender = resp.geschlecht || GENDER_EMPTY;
        this.selectedNationality = resp.staatsangehoerigkeit || NATIONALITY_EMPTY;
        if (resp.bemerkung) this.imageTransformer.setNote(resp.bemerkung);

        if (resp.bilddaten) {
          const base64Raw = (typeof resp.bilddaten === 'string') ? resp.bilddaten : this.arrayBufferToBase64(resp.bilddaten);
          const t = resp.transformation;
          const n = Number(t);
          const imgTypeEnum: ImageType = (! isNaN(n) && (n === ImageType.ORIG_IMAGE || n === ImageType.CANVAS_IMAGE))
            ? n as ImageType
            : ImageType.CANVAS_IMAGE;

          this.identificationBinning(base64Raw, imgTypeEnum, requestId);
        } else {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'label.NoImageInRequest');
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error loading search');
      }
    );
  }

  private arrayBufferToBase64(buffer: any): string {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
  }

  onChangedIncidentMatch(evt: any) {
    if (! evt) {
      return;
    }
    let match: Match = evt.value ?  evt.value : evt;
    this.loadPersonImage(match);
  }

  updateSelectionOnScroll(event: KeyboardEvent) {
    event.preventDefault();
    const direction = event.keyCode === 38 ? -1 : event.keyCode === 40 ?  1 : 0;
    if (direction === 0) return;

    let index = this.relatedCases.findIndex(item => item.value === this.selectedCaseId) + direction;
    index = Math.max(0, Math.min(this.relatedCases.length - 1, index));
    this.selectedCaseId = this.relatedCases[index].value;

    const selectedElement = document.querySelector('.ui-listbox-item.ui-state-highlight');
    if (selectedElement) {
      selectedElement.scrollIntoView({ behavior: 'instant', block: 'start' });
    }

    this.onChangedIncidentMatch({ value: this.selectedCaseId });
  }

  protected exportSingle(isFull: boolean) {
    this.busy = true;
    let searchReportRequest = new SearchReportRequest();
    searchReportRequest.imageOid = this.selectedCaseId.caseID;
    searchReportRequest.extImageOriginal = this.imageTransformer.getOriginalImage(true);
    searchReportRequest.extImageOptimized = this.imageTransformer.getModifiedImage(true);
    searchReportRequest.marisImageOptimized = this.imageTransformer.getModifiedImage(false);
    searchReportRequest.compImageOptimized = this.imageTransformer.getModifiedImage(false);
    searchReportRequest.note = this.imageTransformer.getNote();
    searchReportRequest.score = this.selectedCaseId.score;
    searchReportRequest.lang = this.translate.getBrowserLang();
    searchReportRequest.isFullName = this.exportFullName;
    searchReportRequest.isFull = isFull;
    searchReportRequest.isWord = false;

    this.reportService.createSearchExport(searchReportRequest).subscribe(
      response => {
        if (response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        } else {
          saveAs(response, 'LIBIAS_EXT_PKZ_' + this.currentPKZ + (isFull ? '_mit' : '_ohne') + '_Trefferwert.pdf');
          this.busy = false;
        }
      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
      }
    );
  }

  protected exportBulk() {
    this.busy = true;
    let searchBulkReportRequest = new SearchBulkReportRequest();
    searchBulkReportRequest.extImage = this.imageTransformer.getModifiedImage(true);
    searchBulkReportRequest.note = this.imageTransformer.getNote();
    searchBulkReportRequest.lang = this.translate.getBrowserLang();
    searchBulkReportRequest.isFullName = this.exportFullName;
    searchBulkReportRequest.imageOidList = this.caseIdList;
    searchBulkReportRequest.scoreList = this.scoreList;

    this.reportService.createSearchBulkExport(searchBulkReportRequest).subscribe(
      response => {
        if (response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        } else {
          saveAs(response, 'LIBIAS_Suchergebnis.xlsx');
          this.busy = false;
        }
      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
      }
    );
  }

  private annotateReq = (left: boolean, img: string, isCallingFirstTime: boolean = false): void => {
    this.busy = true;

    this.gesService.analyzeImage(img).subscribe(
      (resp: GesAnalyseResponse) => {
        this.busy = false;

        if (!resp || !resp.embedding) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.NoFaceDetected');
          return;
        }

        if (resp.bbox && resp.bbox.length >= 5) {
          const x_top = resp.bbox[0];
          const y_top = resp.bbox[1];
          const x_bottom = resp.bbox[2];
          const y_bottom = resp.bbox[3];
          const alpha = resp.bbox[4] || 0;

          const width = x_bottom - x_top;
          const height = y_bottom - y_top;
          const centerX = x_top + width / 2;
          const centerY = y_top + height / 2;

          this.imageTransformer.annotateCanvas(
            left,
            width,
            height,
            alpha,
            centerX,
            centerY,
            isCallingFirstTime ?  0 : 1,
            false
          );
        }

        if (resp.landmarks && resp.landmarks.length >= 10) {
          const landmarks = {
            leftEye: { x: resp.landmarks[0], y: resp.landmarks[1] },
            rightEye: { x: resp.landmarks[2], y: resp.landmarks[3] },
            noseTip: { x: resp.landmarks[4], y: resp.landmarks[5] },
            leftMouthCorner: { x: resp.landmarks[6], y: resp.landmarks[7] },
            rightMouthCorner: { x: resp.landmarks[8], y: resp.landmarks[9] }
          };
          this.imageTransformer.setAllLandmarks(left, landmarks);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GesAnalysisFailed');
      }
    );
  };

  private identificationBinning(img: string, imageType: ImageType, requestId?: number) {
    if (this.maxCandidates > this.MAX_CANDIDATES) {
      this.maxCandidates = this.MAX_CANDIDATES;
    }
    if (this.maxCandidates < this.MIN_CANDIDATES) {
      this.maxCandidates = this.MIN_CANDIDATES;
    }
    if (this.minScore > this.MAX_SCORE) {
      this.minScore = this.MAX_SCORE;
    }
    if (this.minScore < this.MIN_SCORE) {
      this.minScore = this.MIN_SCORE;
    }

    let req = new IdentificationBinningRequest();
    req.img = img;
    req.maxMatches = this.maxCandidates;
    req.minScore = this.minScore;

    if (requestId !== undefined && requestId !== null) {
      req.requestId = requestId;
    }

    this.busy = true;

    this.relatedCases = [];
    this.relatedPersonsInfo = [];
    this.caseIdList = [];
    this.scoreList = [];
    this.selectedCaseId = undefined;
    this.searchRestricted = false;

    this.cognitecService.identificationBinning(req).subscribe(
      resp => {
        this.busy = false;

        let foundFace = false;
        if (resp.val && resp.val.processedImage && resp.val.processedImage.foundFace) {
          foundFace = true;

          let faceLoc = resp.val.processedImage.faceLocation;
          if (faceLoc && faceLoc.leftEye && faceLoc.leftEye.value &&
            faceLoc.rightEye && faceLoc.rightEye.value) {
            var obj = {
              left: { x: faceLoc.rightEye.value.x, y: faceLoc.rightEye.value.y, set: 1 },
              right: { x: faceLoc.leftEye.value.x, y: faceLoc.leftEye.value.y, set: 1 }
            };
            this.imageTransformer.loadImageAnnotated(true, obj);
          }
        }

        let matches = resp.val.matches.m;
        let oids: number[] = [];

        if (matches.length === 0) {
          this.busy = false;
          if (foundFace) {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'label.NoCandidatesFound');
          } else {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'label.FaceNotFound');
          }
          return;
        }

        for (let match of matches) {
          oids.push(match.caseID);
        }

        this.busy = true;
        let dateNow = new Date();
        let personsRequest = new PersonsRequest();
        personsRequest.oids = oids;

        this.personService.getPersons(personsRequest).subscribe(
          respPersons => {
            if (! this.utils.containsNullOnly(respPersons)) {
              for (let match of matches) {
                for (let person of respPersons) {
                  if (String(person.imageOid) === String(match.caseID)) {
                    if (this.selectedGender !== GENDER_EMPTY && this.selectedGender !== person.gender) {
                      this.searchRestricted = true;
                      continue;
                    }
                    if (this.selectedNationality !== NATIONALITY_EMPTY && this.selectedNationality !== person.nationality) {
                      this.searchRestricted = true;
                      continue;
                    }
                    if (this.minAge && ! person.age && person.birthDate) {
                      let birthDateStr = person.birthDate.toString();
                      person.age = dateNow.getFullYear() - parseInt(birthDateStr.substring(0, 4));
                      let monthDiff = dateNow.getMonth() + 1 - parseInt(birthDateStr.substring(5, 7));
                      let dayDiff = dateNow.getDate() - parseInt(birthDateStr.substring(8, 10)) - 1;
                      if (monthDiff < 0 || monthDiff === 0 && dayDiff < 0) {
                        person.age = person.age - 1;
                      }
                    }
                    if (! this.minAge || person.age >= this.minAge) {
                      if (this.selectedCaseId === undefined) {
                        this.selectedCaseId = match;
                      }
                      let lbl = String(person.pkz) + ' - ' + this.utils.floorFigure(match.score * 100.0, 2);
                      this.relatedCases.push({ label: lbl, value: match });
                      this.relatedPersonsInfo.push(person);
                      this.caseIdList.push(match.caseID);
                      this.scoreList.push(match.score);
                      break;
                    } else {
                      this.searchRestricted = true;
                    }
                  }
                }

                if (this.relatedCases.length >= this.maxCandidates) {
                  break;
                }
              }

              if (this.selectedCaseId) {
                this.onChangedIncidentMatch(this.selectedCaseId);
              }
              this.busy = false;
            } else {
              this.busy = false;
              this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
            }
          }, err2 => {
            this.busy = false;
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
          });
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
      }
    );
  }

  private fillGender() {
    this.gender = [];
    this.gender.push({ label: GENDER_EMPTY, value: GENDER_EMPTY });
    this.gender.push({ label: GENDER_M, value: GENDER_M });
    this.gender.push({ label: GENDER_W, value: GENDER_W });
    this.gender.push({ label: GENDER_D, value: GENDER_D });
    this.gender.push({ label: GENDER_U, value: GENDER_U });
    this.gender.push({ label: GENDER_X, value: GENDER_X });
  }

  private fillNationalities() {
    this.nationalities = [];
    this.nationalities.push({ label: NATIONALITY_EMPTY, value: NATIONALITY_EMPTY });
    this.incidentService.getNationalitiesSearcher().subscribe(
      response => {
        for (let c of response) {
          if (this.utils.isNotBlank(c)) {
            this.nationalities.push({ label: c, value: c });
          }
        }
      }
    );
  }

  private loadPersonImage(m: Match) {
    this.busy = true;

    this.imageService.getImage(m.caseID).subscribe(
      resp => {
        if (resp && resp.imageData) {
          const imageDataUrl = 'data:image/png;base64,' + resp.imageData;
          this.imageTransformer.loadImage(true, imageDataUrl);
        } else {
          this.imageTransformer.resetImage(true);
        }

        let person: PersonResponse = this.relatedPersonsInfo.filter(function (item) {
          return String(item.imageOid) === String(m.caseID);
        })[0];
        this.showPersonalInfo(person);

        this.cognitecService.getImage(m.caseID).subscribe(
          cogResp => {
            if (cogResp && cogResp.eyelx && cogResp.eyely && cogResp.eyerx && cogResp.eyery) {
              var obj = {
                left: { x: cogResp.eyerx, y: cogResp.eyery, set: 1 },
                right: { x: cogResp.eyelx, y: cogResp.eyely, set: 1 }
              };
              this.imageTransformer.loadImageAnnotated(true, obj);
            }

            this.busy = false;

            var matchScore = parseFloat(this.utils.floorFigure(m.score * 100.0, 2));
            this.imageTransformer.setScore(matchScore);
          },
          err => {
            this.busy = false;
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
          }
        );
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
      }
    );
  }

  private showPersonalInfo(response: PersonResponse) {
    if (! response) {
      return;
    }
    let geschlechtTrans = null;
    this.translate.get('label.Sex.' + response.gender).subscribe(v => { geschlechtTrans = v; });
    let values: string[] = [
      response.pkz !== null ? response.pkz.toString() : '',
      response.fileNumber,
      response.applicantType,
      response.lastName,
      response.firstName,
      response.birthDate !== null ? this.datePipe.transform(response.birthDate, 'dd.MM.yyyy') : '',
      response.birthPlace,
      response.nationality,
      response.gender !== null ? geschlechtTrans : '',
      response.azrNumber,
      response.dNumber,
      response.dateModified !== null ? this.datePipe.transform(response.dateModified, 'dd.MM.yyyy') : ''
    ];

    this.currentPKZ = response.pkz;

    const infoTableHeaders = ['PKZ', 'Aktenzeichen', 'Antragstyp', 'Familienname', 'Vorname', 'Geburtsdatum', 'Geburtsort',
      'Staatsangehörigkeit', 'Geschlecht', 'AZR-Nummer', 'D-Nummer', 'Aufnahmedatum (MARiS-Bild)'];

    const infoTableData = this.createInfoTable(infoTableHeaders, values);

    this.imageTransformer.setTableData(true, infoTableData);

    this.exportEnabled = true;
  }

  private createInfoTable(headers: string[], values: string[]): any[] {
    const result: any[] = [];
    for (let i = 0; i < headers.length; i++) {
      result.push({
        dataKey: headers[i],
        value: values[i] || ''
      });
    }
    return result;
  }
}
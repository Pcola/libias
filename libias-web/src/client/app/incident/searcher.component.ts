declare var ImgCompare: any;

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import {
  CognitecService,
  ImageService,
  IncidentService,
  LoginService,
  PersonService,
  ReportService,
  Utils
} from '../shared/service/index';

import { Message, SelectItem } from 'primeng/primeng';
import { AnalyzePortraitRequest, IdentificationBinningRequest } from '../shared/model/cognitec/index';
import { PersonResponse, PersonsRequest } from '../shared/model/person/index';
import { SearchReportRequest } from '../shared/model/report/search-report-request';
import { SearchBulkReportRequest } from '../shared/model/report/search-bulk-report-request';
import { Match } from '../shared/model/cognitec/identificationBinning-response.model';
import { ImageType } from '../shared/model/cognitec/imageType.enum';
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
  PAGE_WIDTH,
  ROLE_ADMIN,
  ROLE_SEARCHER
} from '../shared/constants';

import { Config } from '../shared/config/env.config';
import { DatePipe } from '@angular/common';

declare var saveAs: any;
declare var base64: any;

@Component({
  moduleId: module.id,
  templateUrl: 'searcher.component.html',
  providers: [DatePipe]
})
export class SearcherComponent implements OnInit {
  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  busy: boolean = false;
  img1 = new Image;
  img2 = new Image;
  imgCmp: any;
  selectedCaseId: Match;
  relatedCases: SelectItem[];
  infoTableHeaders = ['PKZ', 'Aktenzeichen', 'Antragstyp', 'Familienname', 'Vorname', 'Geburtsdatum', 'Geburtsort',
    'Staatsangeh\u00F6rigkeit', 'Geschlecht', 'AZR-Nummer', 'D-Nummer', 'Aufnahmedatum (MARiS-Bild)'];
  infoTableValues = ['', '', '', '', '', '', '', '', '', '', '', ''];
  exportEnabled = false;
  exportFullName = false;
  searchRestricted = false;
  currentPKZ: number = -1;
  geschlechtTrans: string;
  geburtsdatumFmt: string;
  dateModifiedFmt: string;
  relatedPersonsInfo: PersonResponse[];
  caseIdList: number[];
  scoreList: number[];
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

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private loginService: LoginService,
    private cognitecService: CognitecService,
    private imageService: ImageService,
    private reportService: ReportService,
    private personService: PersonService,
    private datePipe: DatePipe,
    private incidentService: IncidentService
  ) {
  }

  ngOnInit() {
    this.relatedCases = [];
    this.relatedPersonsInfo = [];
    this.caseIdList = [];
    this.scoreList = [];
    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_SEARCHER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.initImgCompare();
      this.fillGender();
      this.fillNationalities();
    }
  }

  onChangedIncidentMatch(evt: any) {
    if (!evt) {
      return;
    }
    console.debug('Selected case: ' + JSON.stringify(evt));
    let match: Match = evt.value ? evt.value : evt;
    this.loadPersonImage(match);
  }

  /**
   * Allow the user to scroll through the list of related cases using the up and down arrow keys.
   * Update the scroll position to keep the selected item in view if it is off-screen.
   *
   * @param event KeyDown or Up event to handle
   */
  updateSelectionOnScroll(event: KeyboardEvent) {
    // Prevent default behavior
    event.preventDefault();

    // Determine direction based on key code (38 = Up, 40 = Down)
    const direction = event.keyCode === 38 ? -1 : event.keyCode === 40 ? 1 : 0;
    if (direction === 0) return; // Exit if not up or down arrow

    let index = this.relatedCases.findIndex(item => item.value === this.selectedCaseId) + direction;
    // Ensure index stays within bounds
    index = Math.max(0, Math.min(this.relatedCases.length - 1, index));
    this.selectedCaseId = this.relatedCases[index].value;

    // Scroll to selected element
    const selectedElement = document.querySelector('.ui-listbox-item.ui-state-highlight');
    if (selectedElement) {
      selectedElement.scrollIntoView({behavior: 'instant', block: 'start'});
    }

    this.onChangedIncidentMatch({value: this.selectedCaseId});
  }

  protected exportSingle(isFull: boolean) {
    this.busy = true;
    let searchReportRequest = new SearchReportRequest();
    searchReportRequest.imageOid = this.selectedCaseId.caseID;
    searchReportRequest.extImageOriginal = this.imgCmp.getOriginalImage(1);
    searchReportRequest.extImageOptimized = this.imgCmp.getModifiedImage(1);
    searchReportRequest.marisImageOptimized = this.imgCmp.getModifiedImage(0);
    searchReportRequest.compImageOptimized = this.imgCmp.getImageFromCmp();
    searchReportRequest.note = this.imgCmp.getNotesFromTxtArea();
    searchReportRequest.score = this.selectedCaseId.score; // interval 0-1
    searchReportRequest.lang = this.translate.getBrowserLang();
    searchReportRequest.isFullName = this.exportFullName;
    searchReportRequest.isFull = isFull;
    searchReportRequest.isWord = false;

    this.reportService.createSearchExport(searchReportRequest).subscribe(
      response => {
        if (response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
          console.log('Cannot download report: size 0');
        } else {
          saveAs(response, 'LIBIAS_EXT_PKZ_' + this.currentPKZ + (isFull ? '_mit' : '_ohne') + '_Trefferwert.pdf');
          this.busy = false;
        }
      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        console.log('Cannot download report: ' + err);
      }
    );
  }

  protected exportBulk() {
    this.busy = true;
    let searchBulkReportRequest = new SearchBulkReportRequest();
    searchBulkReportRequest.extImage = this.imgCmp.getModifiedImage(1);
    searchBulkReportRequest.note = this.imgCmp.getNotesFromTxtArea();
    searchBulkReportRequest.lang = this.translate.getBrowserLang();
    searchBulkReportRequest.isFullName = this.exportFullName;
    searchBulkReportRequest.imageOidList = this.caseIdList;
    searchBulkReportRequest.scoreList = this.scoreList;

    this.reportService.createSearchBulkExport(searchBulkReportRequest).subscribe(
      response => {
        if (response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
          console.log('Cannot download report: size 0');
        } else {
          saveAs(response, 'LIBIAS_Suchergebnis.xlsx');
          this.busy = false;
        }
      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        console.log('Cannot download report: ' + err);
      }
    );
  }

  /**
   * add javascript file imgcompare.js as grid for searching(2 images + one compare canvas + buttons)
   * also add method for verifying and annotation
   */
  private initImgCompare() {
    window.addEventListener('dragover', function(e: any) { e = e || event; e.preventDefault(); }, false);
    window.addEventListener('drop', function(e: any) { e = e || event; e.preventDefault(); }, false);

    var bodyRect = document.body.getBoundingClientRect();
    let imgCmpDivId = document.getElementById('imgcmp');
    let rect = imgCmpDivId.getBoundingClientRect();
    let sideMargin = (screen.width - PAGE_WIDTH) / 2;
    let shiftLeft = Math.abs(bodyRect.left) + rect.left;
    this.imgCmp = new ImgCompare(imgCmpDivId, shiftLeft, 265, sideMargin, window.innerHeight, 130, true);
    this.imgCmp.setDebug(0);

    this.imgCmp.setVerifyReq(this.verifyReq.bind(this));
    this.imgCmp.setAnnotateCnvReq(this.annotateReq.bind(this));
    this.imgCmp.enableAnnotation(0);
    this.imgCmp.enableAnnotation(1);
    //this.imgCmp.enableVerify();

    this.imgCmp.enableEyeLines();
    this.imgCmp.enableSyncManual();
    this.imgCmp.disableDrop(0);
    this.imgCmp.enableDrop(1);
    this.imgCmp.disableLock(0);
    this.imgCmp.disableLock(1);
  }

  private verifyReq(_img1: any, _img2: any, imgType : number) {
    this.identificationBinning(_img2, imgType);
  }

  private annotateReq(id: number, img: string) {
    this.analyzePortrait(id, img);
  }

  /**
   *
   * @param id - where to load image (0 - left, 1- right)
   * @param img - image
   *
   * call cognitec to get best image position
   */
  private analyzePortrait(id: number, img: string) {
    let req = new AnalyzePortraitRequest();
    req.img = img;
    this.busy = true;

    this.cognitecService.analyzePortrait(req).subscribe(
      resp => {
        this.busy = false;
        let portrait = resp.val.portraitCharacteristics;
        if (portrait && portrait.leftEye && portrait.rightEye) {
          var res = {
            left: {x: portrait.rightEye.x, y: portrait.rightEye.y, set: 1},
            right: {x: portrait.leftEye.x, y: portrait.leftEye.y, set: 1}
          };

          this.imgCmp.annotateCnv(id, res);
        }
       },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
        console.error('error verificationPortraits: ' + err);
      }
    );
  }

  /**
   *
   * @param img
   * @param imageType
   *
   * find all matches to image added by user, for each result also load person data
   */
  private identificationBinning(img: string, imageType: ImageType) {
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
    req.imgType = imageType;
    req.maxMatches = this.maxCandidates;
    req.minScore = this.minScore;
    this.busy = true;

    this.relatedCases = [];
    this.relatedPersonsInfo = [];
    this.caseIdList = [];
    this.scoreList = [];
    this.selectedCaseId = undefined;
    this.searchRestricted = false;

    if (this.exportEnabled) {
      this.imgCmp.loadImg(0, '');
      this.imgCmp.resetImg(0);
      this.imgCmp.setScore(this.utils.floorFigure(0.0, 2));
      this.imgCmp.createInfoTable(this.infoTableHeaders, this.infoTableValues);
      this.exportEnabled = false;
    }

    this.cognitecService.identificationBinning(req).subscribe(
      resp => {
        this.busy = false;

        // fill Matches
        let matches = resp.val.matches.m;
        let oids: number[] = [];

        if (matches.length > 0) {
          for (let match of matches) {
            oids.push(match.caseID);
          }
        }

        let faceLoc = resp.val.processedImage ? resp.val.processedImage.faceLocation : undefined;
        if (faceLoc && faceLoc.leftEye && faceLoc.leftEye.value && faceLoc.rightEye && faceLoc.rightEye.value) {
          var res = {
            left: {x: faceLoc.rightEye.value.x, y: faceLoc.rightEye.value.y, set: 1},
            right: {x: faceLoc.leftEye.value.x, y: faceLoc.leftEye.value.y, set: 1}
          };

          this.imgCmp.annotateCnv(1, res, resp.imgType);
        }

        // find persons for image oids
        if (matches.length > 0) {
          this.busy = true;
          let dateNow = new Date();
          let personsRequest = new PersonsRequest();
          personsRequest.oids = oids;

          this.personService.getPersons(personsRequest).subscribe(
            respPersons => {
              if (!this.utils.containsNullOnly(respPersons)) {
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
                      if (this.minAge && !person.age && person.birthDate) {
                        let birthDateStr = person.birthDate.toString();
                        person.age = dateNow.getFullYear() - parseInt(birthDateStr.substring(0, 4));
                        let monthDiff = dateNow.getMonth() + 1 - parseInt(birthDateStr.substring(5, 7));
                        let dayDiff = dateNow.getDate() - parseInt(birthDateStr.substring(8, 10)) - 1;
                        if (monthDiff < 0 || monthDiff === 0 && dayDiff < 0) {
                          person.age = person.age - 1;
                        }
                        //console.log('date of birth: ' + birthDateStr + ', computed age: ' + person.age);
                      }
                      if (!this.minAge || person.age >= this.minAge) {
                        if (this.selectedCaseId === undefined) {
                          this.selectedCaseId = match;
                        }
                        let lbl = String(person.pkz) + ' - ' + this.utils.floorFigure(match.score * 100.0, 2) + '%';
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

                this.onChangedIncidentMatch(this.selectedCaseId);
                this.busy = false;
              } else {
                this.busy = false;
                this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
                console.error('Array inside response contains only null values.');
              }
            }, err2 => {
              this.busy = false;
              this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
              console.error('Cannot get images. ' + err2);
            });
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
        console.error('Error getting identificationBinning: ' + err);
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
      },
      err => {
        console.log('Error getting list of nationalities: ' + err);
      }
    );
  }

  /**
   * Get left person image by given caseID and set score.
   */
  private loadPersonImage(m: Match) {
      this.imageService.getImage(m.caseID).subscribe(
      resp => {
        if (resp && resp.imageData) {
          this.imgCmp.loadImg(0, 'data:image/png;base64,' + resp.imageData);
        } else {
          this.imgCmp.loadImg(0, '');
          this.imgCmp.resetImg(0);
        }
        this.imgCmp.setScore(this.utils.floorFigure(m.score * 100.0, 2));
        let person: PersonResponse = this.relatedPersonsInfo.filter(function(item) {return String(item.imageOid) === String(m.caseID);})[0];
        this.showPersonalInfo(person);

        //if (Config.ENV !== 'DEV') {
          this.cognitecService.getImage(m.caseID).subscribe(
            resp => {
              this.busy = false;
              if (resp && resp.eyelx && resp.eyely && resp.eyerx && resp.eyery) {
                var obj = { left: { x: resp.eyerx, y: resp.eyery }, right: { x: resp.eyelx, y: resp.eyely } };
                this.imgCmp.annotateImg(0, obj);
              } else {
                this.imgCmp.resetImg(0);
              }
            },
            err => {
              this.busy = false;
              this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
              console.log('Cannot get Cognitec image: ' + err);
              this.utils.isErrorForbidden(err);
            }
          );
        //}
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
        console.log('Cannot get image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  private showPersonalInfo(response: PersonResponse) {
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
    this.imgCmp.createInfoTable(this.infoTableHeaders, values);
    this.exportEnabled = true;
  }

  private getComponentPos(idx: number): any {
    let pos = this.imgCmp.getPositionCmp();
    if (idx < 3) {
      return {
        'left': pos.x + 'px',
        'top': (pos.y + pos.h + 15 + idx * 40) + 'px',
        'width': pos.w + 'px'
      };
    } else if (idx === 3) {
      return {
        'left': pos.x + 'px',
        'top': (pos.y + pos.h + 135) + 'px',
        'width': pos.w + 'px',
        'text-align': 'center'
      };
    } else {
      return {};
    }
  }
}

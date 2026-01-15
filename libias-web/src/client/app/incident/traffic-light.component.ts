import { Component, OnInit } from '@angular/core';
import { Message } from 'primeng/primeng';
import { TranslateService } from 'ng2-translate';

import { TrafficLight } from '../shared/model/traffic-light/traffic-light.model';
import { TrafficLightService, LoginService, Utils } from '../shared/service/index';
import {
    GROWL_LIFE,
    GROWL_SEVERITY_ERROR,
    GROWL_SEVERITY_SUCCESS, // opravime
    ROLE_ADMIN,
    ROLE_SUPERUSER
} from '../shared/constants';

@Component({
    moduleId: module.id,
    templateUrl: 'traffic-light.component.html',
})
export class TrafficLightComponent implements OnInit {

    growlLife = GROWL_LIFE;
    msgs: Message[] = [];
    busy: boolean = false;
    trafficLights: TrafficLight[] = [];
    editingRowId: number = null;

    roles: any[] = [
        { id: 1, name: 'Admin' },
        { id: 2, name: 'Superuser' },
        { id: 3, name: 'User' },
        { id: 4, name: 'Aussenstelleuser' },
        { id: 5, name: 'Comparer' },
        { id: 6, name: 'Searcher' }
    ];

    constructor(
        private trafficLightService: TrafficLightService,
        private translate: TranslateService,
        private utils: Utils,
        private loginService: LoginService
    ) { }

    ngOnInit() {
        if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_SUPERUSER, ROLE_ADMIN])) {
            this.loginService.logout(true);
        } else {
            this.loadTrafficLights();
        }
    }

    loadTrafficLights() {
        this.busy = true;
        this.trafficLightService.getTrafficLights().subscribe(
            response => {
                this.trafficLights = response;
                this.busy = false;
            }, error => {
                this.busy = false;
                this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.LoadTrafficLights');
            }
        );
    }

    getRoleName(roleId: number): string {
        const role = this.roles.find(r => r.id === roleId);
        return role ? role.name : 'Unknown';
    }

    addNewRow() {
        const newTrafficLight = new TrafficLight();
        this.trafficLights.push(newTrafficLight);
        this.editingRowId = null;
    }

    editRow(trafficLight: TrafficLight) {
        this.editingRowId = trafficLight.roleId;
    }

    saveRow(rowData: TrafficLight) {
        if (!rowData.roleId) {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.RoleIdRequired');
            return;
        }

        if (rowData.scoreFrom === null || rowData.scoreFrom === undefined ||
            rowData.scoreTo === null || rowData.scoreTo === undefined) {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ScoresRequired');
            return;
        }

        if (rowData.scoreFrom >= rowData.scoreTo) {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ScoreFromMustBeLessThanScoreTo');
            return;
        }

        this.busy = true;

        if (!rowData.roleId || this.editingRowId === null) {
            this.trafficLightService.createTrafficLight(rowData).subscribe(
                response => {
                    const index = this.trafficLights.indexOf(rowData);
                    if (index !== -1) {
                        this.trafficLights[index] = response;
                    }
                    this.editingRowId = null;
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_SUCCESS, 'label.Success', 'msg.TrafficLightCreated');
                }, error => {
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.SaveTrafficLight');
                }
            );
        } else {
            this.trafficLightService.updateTrafficLight(rowData.roleId, rowData).subscribe(
                response => {
                    const index = this.trafficLights.findIndex(t => t.roleId === response.roleId);
                    if (index !== -1) {
                        this.trafficLights[index] = response;
                    }
                    this.editingRowId = null;
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_SUCCESS, 'label.Success', 'msg.TrafficLightUpdated');
                }, error => {
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.SaveTrafficLight');
                }
            );
        }
    }

    deleteRow(trafficLight: TrafficLight) {
        if (!trafficLight.roleId) {
            const index = this.trafficLights.indexOf(trafficLight);
            if (index !== -1) {
                this.trafficLights.splice(index, 1);
            }
            this.editingRowId = null;
            return;
        }

        if (confirm(this.translate.instant('msg.ConfirmDelete'))) {
            this.busy = true;
            this.trafficLightService.deleteTrafficLight(trafficLight.roleId).subscribe(
                response => {
                    const index = this.trafficLights.indexOf(trafficLight);
                    if (index !== -1) {
                        this.trafficLights.splice(index, 1);
                    }
                    this.editingRowId = null;
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_SUCCESS, 'label.Success', 'msg.TrafficLightDeleted');
                }, error => {
                    this.busy = false;
                    this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.DeleteTrafficLight');
                }
            );
        }
    }

    cancelEdit(rowData?: TrafficLight) {
        if (rowData && !rowData.roleId) {
            const index = this.trafficLights.indexOf(rowData);
            if (index !== -1) {
                this.trafficLights.splice(index, 1);
            }
        }
        this.editingRowId = null;
    }

    isEditing(rowData: TrafficLight): boolean {
        return (rowData.roleId === this.editingRowId) || (!rowData.roleId && this.editingRowId == null);
    }
}
import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../shared/service';
import { ConfirmationService } from 'primeng/primeng';

@Component({
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  notifications: any[] = [];

  constructor(
    private notificationService: NotificationService,
    private confirmationService: ConfirmationService
  ) { }

  ngOnInit(): void {
    this.fetchNotifications();
  }

  fetchNotifications(): void {
    this.notificationService.getNotifications().subscribe(
      (notifications: any[]) => {
        this.notifications = notifications;
      },
      (error: any) => {
        console.error('Failed to fetch notifications:', error);
      }
    );
  }

  editNotification(notification: any): void {
    this.notificationService.editNotification(notification);
  }

  deleteNotification(notification: any): void {
    this.confirmationService.confirm({
      header: 'Confirmation',
      message: 'Are you sure you want to delete this notification?',
      accept: () => {
        this.notificationService.deleteNotification(notification.id).subscribe(
          () => {
            this.notifications = this.notifications.filter(n => n.id !== notification.id);
          },
          (error : any) => {
            console.error('Failed to delete notification:', error);
          }
        );
      }
    });
  }
}

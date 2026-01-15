export class TrafficLight {
    roleId: number;
    scoreFrom: number;
    scoreTo: number;
    commentRed: string;
    commentYellow: string;
    commentGreen: string;

    constructor(
        roleId?: number,
        scoreFrom?: number,
        scoreTo?: number,
        commentRed?: string,
        commentYellow?: string,
        commentGreen?: string
    ) {
        this.roleId = roleId;
        this.scoreFrom = scoreFrom;
        this.scoreTo = scoreTo;
        this.commentRed = commentRed;
        this.commentYellow = commentYellow;
        this.commentGreen = commentGreen;
    }
}
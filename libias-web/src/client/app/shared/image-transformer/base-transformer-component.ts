export abstract class BaseTransformerComponent {
  synchronized = false;
  monochrome = false;
  helpLines = true;

  height = 0;
  width = 0;

  betweenEyes = 0;
  fromLeft = 0;
  fromTop = 0;

  abstract changeSynchronization(): void;

  abstract changeMonochrome(): void;

  abstract changeHelpLinesVisibility(): void;

  constructLine(context: CanvasRenderingContext2D, fromX: number, fromY: number, toX: number, toY: number, dash: number[], color: string) {
    context.beginPath();
    context.strokeStyle = color;
    context.setLineDash(dash);
    context.moveTo(fromX, fromY);
    context.lineTo(toX, toY);
    context.stroke();
  }
}

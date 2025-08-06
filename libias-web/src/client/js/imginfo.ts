'use strict';

interface Position {
  x: number;
  y: number;
  w: number;
  h: number;
}

interface TableData {
  headers: string[];
  values: (string | null)[];
}

class ImgInfo {
  private elem: HTMLElement;
  private lx: number = 0;
  private ly: number = 0;
  private lw: number = 0;
  private lh: number = 0;
  private rx: number = 0;
  private ry: number = 0;
  private rw: number = 0;
  private rh: number = 0;
  private tbl: HTMLTableElement | null = null;
  private txtBox: HTMLTextAreaElement | null = null;
  private readonly cellInfoID: string = 'cellInfoID';
  private readonly tableName: string = 'Personendaten in MARiS';
  private readonly textBoxHolder: string = 'Bemerkung zum externen Bild';

  constructor(elem: HTMLElement) {
    this.elem = elem;
  }

  init(l: Position, r: Position): void {
    this.lx = l.x;
    this.ly = l.y;
    this.lw = l.w;
    this.lh = l.h;
    this.rx = r.x;
    this.ry = r.y;
    this.rw = r.w;
    this.rh = r.h;
    this.setTablePosition();
    this.setTextBoxPosition();
  }

  createOrChangeInfoTable(headers: string[], values: (string | null)[]): void {
    if (this.tbl) {
      this.elem.removeChild(this.tbl);
    }
    
    this.tbl = document.createElement('table');
    const tbody = document.createElement('tbody');
    
    for (let i = 0; i < headers.length; i++) {
      const row = document.createElement('tr');
      let cell = document.createElement('td');
      let text = document.createTextNode(headers[i] + ':');
      cell.appendChild(text);
      cell.id = this.cellInfoID;
      row.appendChild(cell);
      
      cell = document.createElement('td');
      text = document.createTextNode(values[i] != null ? values[i]! : '');
      cell.appendChild(text);
      cell.id = this.cellInfoID;
      row.appendChild(cell);
      tbody.appendChild(row);
    }
    
    this.tbl.appendChild(tbody);
    this.elem.appendChild(this.tbl);
    this.tbl.className = 'personal-info-table';
    this.setTablePosition();
    this.createOrClearTextBox();
    this.setTextBoxPosition();
  }

  private setTablePosition(): void {
    if (this.tbl) {
      this.tbl.style.left = this.lx.toString() + 'px';
      this.tbl.style.top = (this.ly + this.lh + 180).toString() + 'px';
      this.tbl.style.width = this.lw.toString() + 'px';
      const cellElement = document.getElementById(this.cellInfoID) as HTMLTableCellElement;
      if (cellElement) {
        cellElement.width = (this.lw / 2).toString();
      }
    }
  }

  private setTextBoxPosition(): void {
    if (this.txtBox && this.tbl) {
      this.txtBox.style.left = this.rx.toString() + 'px';
      this.txtBox.style.top = (this.ry + this.rh + 180).toString() + 'px';
      this.txtBox.style.width = this.rw.toString() + 'px';
      this.txtBox.style.height = this.tbl.offsetHeight.toString() + 'px';
    }
  }

  private createOrClearTextBox(): void {
    if (this.txtBox) {
      this.elem.removeChild(this.txtBox);
    }
    
    this.txtBox = document.createElement('textarea');
    this.elem.appendChild(this.txtBox);
    this.txtBox.className = 'jsman personal-info-notes';
    this.txtBox.placeholder = this.textBoxHolder;
  }

  getTxtBoxValue(): string {
    if (this.txtBox) {
      return this.txtBox.value;
    }
    return '';
  }
}

// Export for module usage
export { ImgInfo };

// Keep global function for backward compatibility
declare global {
  interface Window {
    ImgInfo: typeof ImgInfo;
  }
}

if (typeof window !== 'undefined') {
  window.ImgInfo = ImgInfo;
}
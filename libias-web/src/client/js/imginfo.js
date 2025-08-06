'use strict';
function ImgInfo(_elem) {
	var self = this;
	var elem = _elem;
	var lx = 0, ly = 0, lw = 0, lh = 0;
	var rx = 0, ry = 0, rw = 0, rh = 0;
	var	tbl, txtBox;
	var cellInfoID = 'cellInfoID';
    var tableName = 'Personendaten in MARiS';
	var textBoxHolder = 'Bemerkung zum externen Bild';
    
	this.init = function(l, r) {
		lx = l.x; ly = l.y; lw = l.w; lh = l.h;
		rx = r.x; ry = r.y; rw = r.w; rh = r.h;
		setTablePosition();
		setTextBoxPosition();
	};	
	this.createOrChangeInfoTable = function(headers, values) {
		if (tbl) {
			elem.removeChild(tbl);			
		} 
		tbl = document.createElement('table');
		var tbody = document.createElement('tbody');
		//tbl.appendChild(document.createElement('caption').appendChild(document.createTextNode(tableName)));
		for (var i=0; i<headers.length; i++) {
			var row = document.createElement('tr'), cell = document.createElement('td'), text = document.createTextNode(headers[i] + ':');
			cell.appendChild(text); cell.id = cellInfoID; row.appendChild(cell);
			cell = document.createElement('td'); text = document.createTextNode(values[i] != null ? values[i] : '');
			cell.appendChild(text); cell.id = cellInfoID; row.appendChild(cell); tbody.appendChild(row);
		}
		tbl.appendChild(tbody);
		elem.appendChild(tbl);
		tbl.className = 'personal-info-table';
		setTablePosition();
		createOrClearTextBox();	
		setTextBoxPosition();
	};	
	
	function setTablePosition() {
		if (tbl) {
			tbl.style.left = (lx).toString() + 'px'; 
			tbl.style.top = (ly + lh + 180).toString() + 'px';
			tbl.style.width = lw.toString() + 'px';
			document.getElementById(cellInfoID).width = lw/2;			
		}
	};
	
	function setTextBoxPosition() {
		if (txtBox) {
			txtBox.style.left = (rx).toString() + 'px'; 
			txtBox.style.top = (ry + rh + 180).toString() + 'px';
			txtBox.style.width = rw.toString() + 'px';
			txtBox.style.height = tbl.offsetHeight.toString() + 'px';
		}
	};
	
	function createOrClearTextBox() {
		if (txtBox) {
			elem.removeChild(txtBox);
		}
		txtBox = document.createElement('textarea');
		elem.appendChild(txtBox);
		txtBox.className = 'jsman personal-info-notes';
		txtBox.placeholder = textBoxHolder;
	};	
	
	this.getTxtBoxValue = function() {
		if (txtBox) {
			return txtBox.value;
		}
		return "";
	}	
};
'use strict';
function ImgCmp(_elem) {
  var elem = _elem;
  function log(text) { if (self.debug !== 0) console.log('CMP: ' + text); };  
  this.init = function (obj) {
    x = obj.x; y = obj.y; w = obj.w; h = obj.h;
    bw = parseInt(cnvSld.style.borderWidth, 10);
    cnv1.style.left = (x + bw).toString() + 'px';
    cnv2.style.left = (x + bw).toString() + 'px';
    cnvTop.style.left = (x + bw).toString() + 'px';
    cnvSld.style.left = x.toString() + 'px';

    cnv1.style.top = (y + bw).toString() + 'px';
    cnv2.style.top = (y + bw).toString() + 'px';
    cnvTop.style.top = (y + bw).toString() + 'px';
    cnvSld.style.top = y.toString() + 'px';

    cnv1.width = w;
    cnv2.width = w;
    cnvTop.width = w;
    cnvSld.width = w;
    cnvTemp.width = w;

    cnv1.height = h;
    cnv2.height = h;
    cnvTop.height = h;
    cnvSld.height = h;
    cnvTemp.height = h;

    rect = cnvSld.getBoundingClientRect();               //get bounding rectangle for mouse

    ctxTop.beginPath();                                               //draw slider border
    ctxTop.strokeStyle = '#00bfff';
    ctxTop.lineWidth = 2 * r;
    ctxTop.rect(r, r, cnvTop.width - (2 * r), cnvTop.height - (2 * r));
    ctxTop.closePath();
    ctxTop.stroke();

    ctxTop.beginPath();                                               //draw slider path
    ctxTop.strokeStyle = '#0000ff';
    ctxTop.lineWidth = 1;
    ctxTop.rect(r, r, cnvTop.width - (2 * r), cnvTop.height - (2 * r));
    ctxTop.closePath();
    ctxTop.stroke();

    ctx2.fillStyle = '#ffffff';
    betweenEyes = cnvSld.width / 4;
    fromLeft = (cnvSld.width - betweenEyes) / 2;
    fromTop = ((cnvSld.height) / 2.5);
    sliders.setPos(0, cnvSld.width / 2, r);
    sliders.setPos(1, cnvSld.width / 2, cnvSld.height - r);

    log('---------------------------');
    log('INIT');
    log('VER:    ' + self.version);
    log('CNV XY: ' + cnvSld.style.left + ', ' + cnvSld.style.top);
    log('CNV WH: ' + cnvSld.width + ', ' + cnvSld.height);
  };

  this.loadFace1 = function (_img) {
    cnvT.width = _img.width; cnvT.height = _img.height;
    //ctxT.clearRect(0,0, cnvT.width, cnvT.height);
    ctxT.putImageData(_img, 0, 0);
    ctx1.clearRect(0, 0, cnv1.width, cnv1.height);
    ctx1.drawImage(cnvT, 0, 0, cnv1.width, cnv1.height);
//    ctx1.drawImage(cnvT, 0, 0, cnv1.width- (0), cnv1.height- (0));
    update();
  };
  this.loadFace2 = function (_img) {
    cnvT.width = _img.width; cnvT.height = _img.height;
    //ctxT.clearRect(0,0, cnvT.width, cnvT.height);
    ctxT.putImageData(_img, 0, 0);
    ctxTemp.clearRect(0, 0, cnvTemp.width, cnvTemp.height);
    ctxTemp.drawImage(cnvT, 0, 0, cnvTemp.width, cnvTemp.height);
//    ctxTemp.drawImage(cnvT, 2* r, 2* r, cnvTemp.width- (4* r), cnvTemp.height- (4* r));
    update();
  };
  this.disableEyeLines = function () { eyeLines = 0; update(); };
  this.enableEyeLines = function () { eyeLines = 1; update(); };
  
  this.getOptimizedImage = function () {
	var mergedComparisonImage = document.createElement('canvas');
	mergedComparisonImage.width = cnv1.width;
	mergedComparisonImage.height = cnv1.height;
	
	var mergedCtx = mergedComparisonImage.getContext("2d");
	mergedCtx.drawImage(cnv1, 0, 0);
	mergedCtx.drawImage(cnv2, 0, 0);   
	//mergedCtx.drawImage(cnvTop, 0, 0);  //draw blue box
	mergedCtx.drawImage(cnvSld, 0, 0);
		  
	var ximg= mergedComparisonImage.toDataURL('image/png').split(',');
	return( ximg[1] );
  };
  
  function update() {
    var i;
    var poly = sliders.getPoly();
    //log('UPDATE !!!');
    rect = cnvSld.getBoundingClientRect();               //get bounding rectangle for mouse
    ctx2.clearRect(0, 0, cnv2.width, cnv2.height);
    ctx2.save();
    ctx2.beginPath();                                   //cut the split line between pictures
    ctx2.moveTo(poly[0].x, poly[0].y);
    for (i = 1; i < poly.length; i++) {
      ctx2.lineTo(poly[i].x, poly[i].y);
    }
    ctx2.lineTo(poly[0].x, poly[0].y);
    ctx2.closePath();
    ctx2.clip();                                        //clip the split line
    ctx2.fillRect(0, 0, cnv2.width, cnv2.height);
    ctx2.drawImage(cnvTemp, 0, 0);                      //draw clipped face2
    ctx2.restore();                                     //restore context

    ctxSld.clearRect(0, 0, cnvSld.width, cnvSld.height);
    if (eyeLines !== 0) {
      ctxSld.beginPath();                                         //line between sliders
      ctxSld.strokeStyle = '#ff0000';
      ctxSld.lineWidth = 1;
      ctxSld.setLineDash([10, 0]);
      ctxSld.moveTo(sliders.slider[0].x, sliders.slider[0].y);
      ctxSld.lineTo(sliders.slider[1].x, sliders.slider[1].y);
      ctxSld.closePath();
      ctxSld.stroke();

      ctxSld.beginPath();                                         //eye lines
      ctxSld.lineWidth = 1;
      ctxSld.setLineDash([5, 10]);
      ctxSld.strokeStyle = '#00ff00';
      ctxSld.globalAlpha = 1.0;
      ctxSld.moveTo(fromLeft, 0); ctxSld.lineTo(fromLeft, h);
      ctxSld.moveTo(fromLeft + betweenEyes, 0); ctxSld.lineTo(fromLeft + betweenEyes, h);
      //ctxSld.moveTo(0, fromTop); ctxSld.lineTo(cnvSld.width, fromTop);
      ctxSld.moveTo(0, fromTop + (0)); ctxSld.lineTo(cnvSld.width, fromTop + (0));
      ctxSld.closePath();
      ctxSld.stroke();
    }
    for (i = 0; i < sliders.slider.length; i++) {               //redraw slider
      ctxSld.beginPath();
      ctxSld.strokeStyle = '#0000ff';
      ctxSld.setLineDash([10, 0]);
      ctxSld.arc(sliders.slider[i].x, sliders.slider[i].y, r, 0, 2 * Math.PI, false);
      if (sliders.slider[i].selected !== 0) {
        ctxSld.fillStyle = 'white';
      } else {
        ctxSld.fillStyle = '#00bfff';
      }
//      ctx.fillStyle = 'white';    //TODO test if needed change background of selected
      ctxSld.closePath();
      ctxSld.fill();      
      ctxSld.stroke();
    }
  }
  this.mouseMove = function (evt) {                       //mouse handler functions
    //var ofx= elem.scrollLeft; var ofy= elem.scrollTop;
    //console.log('SCROLL XY: '+ ofx+ ', '+ ofy);
    var x = (evt.clientX - rect.left - bw);
    var y = (evt.clientY - rect.top - bw);
    //log('MouseMoveXYD: ' + x +  ', ' + y + ': ' + mDown);
    sliders.mouseMove(x, y);
    update();
  };
  this.mouseDown = function (evt) {var x = evt.clientX - rect.left - bw; var y = evt.clientY - rect.top - bw; mDown = 1; sliders.mouseDown(x, y); };
  this.mouseUp = function (evt) {var x = evt.clientX - rect.left - bw; var y = evt.clientY - rect.top - bw; mDown = 0; sliders.mouseUp(x, y); };
  this.mouseOut = function (evt) {var x = evt.clientX - rect.left - bw; var y = evt.clientY - rect.top - bw; mDown = 0; sliders.mouseUp(x, y); };
  this.mouseOver = function (evt) {};
  
  //Constructor------------------------------------------------------------------------------
  var self = this;
  this.version = '0.0.1';
  this.debug = 0;
  var MSIE = !!navigator.userAgent.match(/Trident/g) || navigator.userAgent.match(/MSIE/g);
  var event = 0;
  if (MSIE) {
    event = 'change';
  } else {
    event = 'input';
  }
  var x, y, w, h, bw;
  var rect;
  var mDown = 0;
  //var mousein= 0;
  var offset = 40;                                                 //border offset
  var eyeLines = 0;
  var r = 10;
  var betweenEyes = 0, fromLeft = 0, fromTop = 0;

  var cnv1 = document.createElement('canvas');
  var cnv2 = document.createElement('canvas');
  var cnvTop = document.createElement('canvas');
  var cnvSld = document.createElement('canvas');
  var cnvTemp = document.createElement('canvas');
  var cnvT = document.createElement('canvas');

  var ctx1 = cnv1.getContext('2d');
  var ctx2 = cnv2.getContext('2d');
  var ctxTop = cnvTop.getContext('2d');
  var ctxSld = cnvSld.getContext('2d');
  var ctxTemp = cnvTemp.getContext('2d');
  var ctxT = cnvT.getContext('2d');

  cnv1.className = 'jsman';
  cnv2.className = 'jsman';
  cnvTop.className = 'jsman';
  cnvSld.className = 'jsman';

  elem.appendChild(cnv1); elem.appendChild(cnv2); elem.appendChild(cnvTop); elem.appendChild(cnvSld);
  cnv1.style.zIndex = 0; cnv2.style.zIndex = 1; cnvTop.style.zIndex = 2; cnvSld.style.zIndex = 3;
  
  cnvSld.title = 'Zum Verschieben des Wischers Punkte am Rand bewegen';

  cnv1.style.border = '1px solid #006AA3';
  cnv2.style.border = '1px solid #006AA3';
  cnvSld.style.border = '1px solid #006AA3';
  cnvTop.style.border = '1px solid #006AA3';

  cnv1.style.borderRadius = '10px';
  cnv2.style.borderRadius = '10px';
  cnvSld.style.borderRadius = '10px';
  cnvTop.style.borderRadius = '10px';
  
  ctx2.fillStyle = '#ffffff';

  var sliders = new Sliders(r);
  sliders.add(0, 0); sliders.add(0, 0);
  sliders.setCnv(cnvSld);

  //TODO: fix to add event listener
  cnvSld.onmousemove = self.mouseMove.bind(self);                    //register mouse callbacks
  cnvSld.onmousedown = self.mouseDown.bind(self);
  cnvSld.onmouseup = self.mouseUp.bind(self);
  cnvSld.onmouseout = self.mouseOut.bind(self);
  cnvSld.onmouseover = self.mouseOver.bind(self);
}
//helper functions
function Sliders(_r) {
  var self = this;
  var r = _r;
  var cnv = null;
  var x = 0, y = 0, lastX = 0, lastY = 0, mouseDown = 0;
  var TL = null, BL = null, TR = null, BR = null;
  this.slider = [];  
  this.add = function (x, y) { self.slider.push(new Slider(x, y)); };
  this.setCnv = function (_cnv) { cnv = _cnv; };
  this.mouseDown = function (_x, _y) { mouseDown = 1; lastX = _x; lastY = _y; check(); };
  this.mouseUp = function (_x, _y) { mouseDown = 0; lastX = _x; lastY = _y; check(); };
  this.mouseMove = function (_x, _y) { lastX = x; lastY = y; x = _x; y = _y; check(); };
  this.setPos = function (i, _x, _y) {
    //console.log('SETPOS IXY= '+ i+ ', '+ _x+ ', '+ _y);
    self.slider[i].x = _x; self.slider[i].y = _y;
  };
  var selected= function () {
    var i;
    for (i = 0; i < self.slider.length; i++) {
      if (self.slider[i].selected !== 0) return (1);
    }
    return (0);
  };
  var check = function () {
    var sel;
    var i;
    for (i = 0; i < self.slider.length; i++) {
      sel = selected();
      if (sel === 0) if (self.slider[i].isIn(x, y) !== 0) self.slider[i].selected = 1;
      if (mouseDown === 0) self.slider[i].selected = 0;
      if (self.slider[i].selected !== 0 && mouseDown !== 0) {
        if (self.slider[i].x === r || self.slider[i].x === cnv.width - r) {
          self.slider[i].y += ((self.slider[i].y + y - lastY) >= r) && ((self.slider[i].y + y - lastY) <= cnv.height - r) ? y - lastY : 0;
        }
        if (self.slider[i].y === r || self.slider[i].y === cnv.height - r) {
          self.slider[i].x += ((self.slider[i].x + x - lastX) >= r) && ((self.slider[i].x + x - lastX) <= cnv.width - r) ? x - lastX : 0;
        }
      }
    }
  };
  function where(i) {
    if (self.slider[i].y === r) return (4);  //top
    if (self.slider[i].y === cnv.height - r) return (2);  //bottom
    if (self.slider[i].x === r) return (3);  //left
    if (self.slider[i].x === cnv.width - r) return (1);  //right
    return (0);
  }
  this.getPoly = function () {
    var poly = [];
    TL = { x: r, y: r };
    BL = { x: r, y: cnv.height - r };
    TR = { x: cnv.width - r, y: r };
    BR = { x: cnv.width - r, y: cnv.height - r };

    if (where(0) === 2 && where(1) === 4) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(TR); poly.push(BR); return (poly); }
    if (where(0) === 4 && where(1) === 2) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(TR); poly.push(BR); return (poly); }

    if (where(0) === 3 && where(1) === 1) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(BR); poly.push(BL); return (poly); }
    if (where(0) === 1 && where(1) === 3) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(BR); poly.push(BL); return (poly); }

    if (where(0) === 1 && where(1) === 4) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(TR); return (poly); }
    if (where(0) === 4 && where(1) === 1) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(TR); return (poly); }

    if (where(0) === 3 && where(1) === 4) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(TR); poly.push(BR); poly.push(BL); return (poly); }
    if (where(0) === 4 && where(1) === 3) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(TR); poly.push(BR); poly.push(BL); return (poly); }

    if (where(0) === 3 && where(1) === 2) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(BR); poly.push(TR); poly.push(TL); return (poly); }
    if (where(0) === 2 && where(1) === 3) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(BR); poly.push(TR); poly.push(TL); return (poly); }

    if (where(0) === 1 && where(1) === 2) { poly.push(self.slider[0]); poly.push(self.slider[1]); poly.push(BR); return (poly); }
    if (where(0) === 2 && where(1) === 1) { poly.push(self.slider[1]); poly.push(self.slider[0]); poly.push(BR); return (poly); }
    poly.push(TR);
    return (poly);
  };
}
function Slider(_x, _y) {
  this.r = 10;
  this.x = _x; this.y = _y; this.selected =  0;
  this.isIn = function (_x, _y) { if (Math.sqrt(Math.pow((this.x - _x), 2) + Math.pow((this.y - _y), 2)) < this.r) return (1); return (0); };
}

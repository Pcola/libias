/*
TODO:
- global colors variables
*/

'use strict';
function ImgMan(_ID, _elem) {
  var self = this; 
  var elem = _elem;
  self.version = '0.0.1';
  self.debug = 0;
  self.ID = _ID;
  var annotationEnabled = 0;
  var MSIE = !!navigator.userAgent.match(/Trident/g) || navigator.userAgent.match(/MSIE/g);
  var event = null;
  if(MSIE)  event = 'change';
  else      event = 'input';
  var dropCB = null;
  var dropCBObj = null;
  self.neighbor = null;
  self.sync = 0;
  self.lock = 1;
  var x = 0, y = 0, w = 0, h = 0; 
	var updateCallBack = null;
  var annotateCnvReq = null;
  
  var rect = null;
  var eyeObj = { left: { x: 0, y: 0, set: 0 }, right: { x: 0, y: 0, set: 0 } };
  var imgCoord = { x: 0, y: 0 };
  var sizeTemp = 0;
  var t = { x: 0, y: 0 };
  var cOffset = { x: 0, y: 0 };
  var cOffsetTmp = { x: 0, y: 0 };

  var scale = 1, angle = 0, br = 0, ct = 1, gs = 0;
  var eyeLines = 0;
  var betweenEyes = 0;
  var fromLeft = 0;
  var fromTop = 0;
  var shift = { x: 0, y: 0 };
	var mouse = { down: 0, lastX: 0, lastY: 0 , pressed: 0};
  
  var img = new Image();
  var fReader = new FileReader();

  var cnv = document.createElement('canvas');
  var cnvO = document.createElement('canvas');
  var cnvTemp = document.createElement('canvas');  
  var ctx = cnv.getContext('2d');
  var ctxO = cnvO.getContext('2d');
  var ctxTemp = cnvTemp.getContext('2d');
  
  cnv.className = 'jsman';
  cnvO.className = 'jsman';
  
//  ctx.imageSmoothingEnabled = false;
//  ctxTemp.imageSmoothingEnabled = false;

  var rImg = document.createElement('img');
  var brImg = document.createElement('img');
  var ctImg = document.createElement('img');
  var annotAImg = document.createElement('img');
  var annotLImg = document.createElement('img');
  var annotRImg = document.createElement('img');
  var reDrawImg = document.createElement('img');
  var reSetImg = document.createElement('img');
  var rSlider = document.createElement('input');
  var brSlider = document.createElement('input');
  var ctSlider = document.createElement('input');

  rImg.className = 'jsman';
  brImg.className = 'jsman';
  ctImg.className = 'jsman';
  annotAImg.className = 'jsman';
  annotLImg.className = 'jsman';
  annotRImg.className = 'jsman';
  reDrawImg.className = 'jsman';
  reSetImg.className = 'jsman';

  rImg.tabIndex = 0;
  brImg.tabIndex = 0;
  ctImg.tabIndex = 0;
  annotAImg.tabIndex = 0;
  annotLImg.tabIndex = 0;
  annotRImg.tabIndex = 0;
  reDrawImg.tabIndex = 0;
  reSetImg.tabIndex = 0;

  
  rSlider.className = 'jsman';
  brSlider.className = 'jsman';
  ctSlider.className = 'jsman';

  rImg.title = 'Bild drehen (zum Zurücksetzen klicken)';
  brImg.title = 'Bildhelligkeit ändern (zum Zurücksetzen klicken)';
  ctImg.title = 'Bildkontrast ändern (zum Zurücksetzen klicken)';

  rSlider.title = rImg.title;
  brSlider.title = brImg.title;
  ctSlider.title = ctImg.title;

  annotAImg.title = 'Augenposition automatisch setzen';
  annotLImg.title = 'Das linke Auge händisch markieren';
  annotRImg.title = 'Das rechte Auge händisch markieren';
  reDrawImg.title = 'Bild normalisieren';
  reSetImg.title = 'Bild zurücksetzen';



  cnvO.title= '\
    Bild verschieben: Klicken und Maus bewegen\n\
    Bildgröße verändern: über Mausrad\n\
    Bild drehen: über Strg + Mausrad\n\
    Bild horizontal verschieben: über X + Mausrad\n\
    Bild vertikal verschieben: über Y + Mausrad\n\
  ';
  rSlider.type = 'range';
  brSlider.type = 'range';
  ctSlider.type = 'range';

  rSlider.className = 'jsman_slider';
  brSlider.className = 'jsman_slider';
  ctSlider.className = 'jsman_slider';

  rSlider.min = -180;
  rSlider.max = 180;
  rSlider.value = 0;
  
  brSlider.min = -100;
  brSlider.max = 100;
  brSlider.value = 0;
  
  ctSlider.min = -100;
  ctSlider.max = 100;
  ctSlider.value = 0;  

  img.addEventListener('load', onLoad, false);
  fReader.addEventListener('load', function (evt) { img.src = evt.target.result; }, false);
  cnvO.addEventListener('dragover', function (evt) { evt.preventDefault(); }, false);
  cnvO.addEventListener('mousedown', function (evt) { if (self.lock != 0) return (-1); mouse.down = 1; mouse.lastX= evt.clientX- rect.left; mouse.lastY= evt.clientY- rect.top;}, false);
  cnvO.addEventListener('mouseup', function (evt) { if (self.lock != 0) return (-1); mouse.down = 0; }, false);
  cnvO.addEventListener('mouseout', function (evt) { if (self.lock != 0) return (-1); mouse.down = 0; }, false);
  cnvO.addEventListener('mousemove', function (evt) { if (self.lock != 0) return (-1); mouseMove(evt); }, false);
  cnvO.addEventListener('mousewheel', function (evt)  { if (self.lock != 0) return (-1); mouseWheel(evt); }, false);
  cnvO.addEventListener('DOMMouseScroll', function (evt)  { if (self.lock != 0) return (-1); mouseWheel(evt); }, false);

  rImg.addEventListener('click', function() { if (self.lock != 0) return (-1); rSlider.value = 0; var dr= rSlider.value - angle; update(0, 0, 0, dr); }, false); 
  brImg.addEventListener('click', function() { if (self.lock != 0) return (-1); brSlider.value = 0; br = +brSlider.value * 255 / 100; update(0, 0, 0, 0); }, false);
  ctImg.addEventListener('click', function() { if (self.lock != 0) return (-1); ctSlider.value = 0; ct = (+ctSlider.value * 1 / 100) + 1; update(0, 0, 0, 0); }, false);
  rSlider.addEventListener(event, function() { if (self.lock != 0) return (-1); var dr = rSlider.value - angle; update(0, 0, 0, dr); }, false);
  brSlider.addEventListener(event, function() { if (self.lock != 0) return (-1); br = +brSlider.value * 255 / 100; update(0, 0, 0, 0); }, false);
  ctSlider.addEventListener(event, function() { if (self.lock != 0) return (-1); ct = (+ctSlider.value * 1 / 100) + 1; update(0, 0, 0, 0); }, false);

  window.addEventListener('keydown', keyDown, false);
  window.addEventListener('keyup', keyUp, false);

  elem.appendChild(cnv);
  elem.appendChild(cnvO);
  elem.appendChild(rImg);
  elem.appendChild(brImg);
  elem.appendChild(ctImg);

  elem.appendChild(rSlider);
  elem.appendChild(brSlider); 
	elem.appendChild(ctSlider);

  rImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAA/1BMVEUAAAAAgP8Abu8AbPAAbfEAbe8AbfAAbfAAbfAAbPAAbvAAa/AAZv8AceMAbfEAbPAAbfAAbvEAbfMAbPAAbfAAbfAAbfEAbOsAbvEAdOgAbfAAbfEAb/QAcewAbfAAbfEAc/IAbfAAbPAAau0AbfAAbe8Abe8AbfEAbvAAbfEAbPAAbe8AbPAAbvAAbfAAb+4AbfAAbfAAbPAAbfAAbO8AbfEAbfAAbfAAZu4AbfAAbfAAbe8AbPAAbvAAbO8AbfAAbfAAAP8AcfEAbvAAbu8AbfAAbfEAbO8AbvEAbfAAbfAAbfMAbe4Abe0AcPMAb+4AbfAAb+8AbvAAbfAAAADMu0XeAAAAU3RSTlMABk94n8Xs89rBlUUFCW7k75AVdujyfhpKC7fDFxv+OBTJVx3YxINbMzZjkchWvjxGzlX8YWzgVA/n+VJTZkD12wESe1HG1FBIu/0qTRwpLmc+uiOLZd0AAAABYktHRACIBR1IAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4QkYDyEgNdwNkAAAAWZJREFUOMvNU9eCgjAQxIK9YO+9gKKeDXvvXe/y//9ySSAYkHu/eWF2Z9gkmyzDqDCZLVbWZnc4XW4P8wmvzw9UcIGgTvaEwkCDSBRWicWJnkiCD6TSGS6bk/V8ARjAzwJQxHqprKQqVV6o1cXG29TEhpYcJL/Iiu0OMXRRyGPa61MHlgaKYQij0RixyZQ6k6guMVOjOX3oxZIY0G8rRAprTVs227Fs2MFgjzYg6Btr6h+Q4YjqRQCQDHrPnM7gckUkeLszxng8mf+C5+MP4X7Dz+Z6AeeTkS7BZ7OA3yNqyKFv0utCD+b3kOzkpo63G42+xo9oBdmUXMxyQRvmOCdCNlPvVqT04QSXHSGqyAOJ2sbrgnM84l1SoNMm8vePnGnhqPl+og2xXhP4akUJyyVsKALAUkP1RiEv18tluUw69aknE2TFeAyOXjSilcMh/QAHAxw1VD6vQe89bpfTYbexVouZOvAvsbqBlmHbnCwAAAAldEVYdGRhdGU6Y3JlYXRlADIwMTctMDktMjRUMTU6MzM6MzIrMDI6MDCAAnE0AAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE3LTA5LTI0VDE1OjMzOjMyKzAyOjAw8V/JiAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAASUVORK5CYII='; 
  brImg.src ='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAXVBMVEUAAAAAbe0AbfAAbfEAbvEAbfAAbu8AbfAAZv8Abu8AbfAAgP8AbfAAbfAAbfAAceMAbfAAa/IAbu8Abu8Abe8AAP8Abe8AbfAAbPAAbPEAbe8AbfAAbfAAbfAAAABvdoZuAAAAHXRSTlMAHMl+kCOjegVfrwRpm/oJnSaAs+YBcPa1f4G37A4Ynb8AAAABYktHRACIBR1IAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4QkYDyQYYKlBSwAAANZJREFUOMudk+0WRCAQhkMI+a5YvPd/mxvOSmuP2eP9UzM9DdPMMOYpCMOA3SkColuAA/wJECep2HcZkO07kSbxASRAXuxuKXe0yIHkAFIApR+0tK70sITFq/p8Xlc2qHB2UVaNXdquV6rvWrttqrLwQtr72gzYNBi9Ob6kRxwa9Y9cDU4y1/N2OANDewE6eHq5+kWcZzah3gd6m3zGeRSwcDUlY8oHFGNyXUMaID9B/uTfaZIPRT01UaxPuad5WebpWm6yYciWI5uWbHunp5PlRA7vdfzfWBgiq42mKB8AAAAldEVYdGRhdGU6Y3JlYXRlADIwMTctMDktMjRUMTU6MzY6MjQrMDI6MDDJUY/UAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE3LTA5LTI0VDE1OjM2OjI0KzAyOjAwuAw3aAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAASUVORK5CYII=';
  ctImg.src ='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAY1BMVEUAAAAAgP8AbvEAbfAAbPAAbfAAbfAAcPUAbvAAbfAAYP8AbPAAbfAAbfAAbfEAbfAAavQAbPAAbfAAbe8AbPAAAP8AbO4AbfAAbfAAbe8Abf8AbfAAcewAbfEAZuYAbfAAAAB5311iAAAAH3RSTlMABkiWwdrzGZfyCIT179S8GMjggTQBLePscAfNG7IKXLvIdwAAAAFiS0dEAIgFHUgAAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfhCRgPIDFGdxwjAAAAyklEQVQ4y4WTWRaDIAxFo4KiYp3H2r7977JoPWirknwB75KQkBBZ8/xAyDCUIvA9OlukYliLVfQnJ6lehOyRb4hOk6NelOasqpuWyHopi13vemAYp3W9x+k7e9/oz3nb4EBsPhLj//WmM4Dy+47U3Lf6D4B0zU9jmOka0Eu2ChjpBoAy9YtRTbdA7JEP1HQLwKcAaBxAQAJZ6wAESTzIAUgKkbuAkAfYEOwj2TTZQrGlZj+L/262YfiWY5uWb3t+cPjRI3Z4F7se/w8VWjz33L2cjAAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0wOS0yNFQxNTozMjo0OSswMjowMGcCR+kAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMDktMjRUMTU6MzI6NDkrMDI6MDAWX/9VAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAABJRU5ErkJggg==';
  
  annotAImg.src = 'data:image/png;base64,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';
  annotLImg.src = 'data:image/png;base64,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';
  annotRImg.src = 'data:image/png;base64,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';
  reDrawImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAA0lBMVEUAAAAAgP8AbfAAbe8AbfAAbfAAbfAAb/QAbfAAbfAAbu8AbfAAbfAAbPAAbvEAa+8Abe8AbfAAbfAAafAAbO4AbfAAbfEAbOsAbfEAau0AbPAAbu4AbfAAbfAAbfAAbvAAa/IAbfAAceMAb/IAgP8Abe8AbfAAbvAAb/EAbPAAbPAAbfAAbvEAbfAAbu8AbfEAbfEAbvAAcO8AbfAAbe4AbPAAbfAAdOgAbfAAbe8AbPAAbfAAgP8Ab+4AbfAAbe0AdusAauoAbfAAbfAAbfAAAADi7g7HAAAARHRSTlMABkaUwNnzF/LsgvT1rl0fxfaIES3jwhqhHd0s8P3JnBPuCScCL3qXNULI3kj4T9RLlTDLTVXoC/eRhMYELscODQy2vIUp8BAAAAABYktHRACIBR1IAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4QoJCTka2kN64wAAAYRJREFUOMuFU+eagkAMXEQQKQIWwN67WLCdvdy8/zMdCiyo3Gd+LJuwmWRSCKHCJNgkx/Nckk0w5FNSrABX0unHKbCpt9+MKEFWMqrryqgZRYYkvqCkNOjZXKjnsjq0CEi+AMN8hTQNFPLUvwDrIy3GQsHHYDRYMVkTC5rnJsKIo0UYA+IzgKSbJFZMXXoEYZGlpqJVKllFqmbBukCCTPmVK6hWUSlTtrLAkAQU6lFDXVXrqFGDgoQbIROoDTRbhLSaaASWjBsjCTVQ255vDe3AoiJJOFCOHR5dQrrgO5QpOMKnQ149yP2+jF5oSfMvDwbVR6+rg8gDKRpiKGDEsiMIwzDEOJLkxOanj++Utyc0yVGE5gxz7zLHjNJchIVysAyQl3CCQq3CUq8hb3yRsfZLrW/DZjmbHyobx2/W7ku7bfPLwOy9y78jdzh6t/+GFqdAiR974HwJ1LjFOZzOOFwp4Pvq2fsjuRyg3ajTy/Lqu2fEq4Z7NK3n+kvj0WK19U23++8fg4E2TLdxFpsAAAAldEVYdGRhdGU6Y3JlYXRlADIwMTctMTAtMDlUMDk6NTc6MjYrMDI6MDA4XhMIAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE3LTEwLTA5VDA5OjU3OjI2KzAyOjAwSQOrtAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAASUVORK5CYII=' ;
  reSetImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAABv1BMVEUAAAAAbfMAbe8AbvAAbfAAbvAAbfAAbPAAbvAAbvEAbe8AbfAAbfAAbu8AZu4AbfEAbfAAbe8AbfAAgP8AbvAAbO8AbPEAbfAAafAAbfAAbfAAavEAceMAbPAAbfAAa+8AbfEAbPAAbPAAcO8AZv8AbO8AAP8AgP8Abe8AbfEAbfEAbfAAbPAAcO8AbPEAbfAAbfAAb/QAbfAAa/IAbPEAbfEAbPAAbfAAbPAAgP8Abe8Abe4AbfAAbe8AbfEAbfAAbPEAbe8AbfAAbfAAbvAAbfAAc/IAbfAAau0Ab/EAbu8AbPEAbfAAbfAAbfAAbvAAbfAAbe8AcO8AbfAAa+0AbfAAbfAAbfAAbfEAbfAAbfAAbfEAbe8AbfAAbe4AcfEAbfAAbfAAbPAAbPEAbvEAbvAAbfAAVf8AbfAAbPAAbPAAbfAAZuYAbfAAbe8AbO8AbfEAbPAAbfAAbvAAbO8AbfEAb+8AbfAAb+4AbfAAbvEAbe0AbfEAbPAAbfAAbfAAb+4Abf8Abe8AbO8Abe8AbfAAa/IAbPAAbfMAbfAAbPAAbfAAbfAAbe8Abe8AbvEAbfAAYP8AbO4AbfAAAAB4dzzKAAAAk3RSTlMAFXOqq6ytrqVIUrzGgA98iIHOBJxhSXUR6fYkCd31H2y1hBAFTgECxFtZ35kgavC9F/gmRzZC2MEG1U16lLDMXIPS93T5FCMdNXJ/2vvXVuE/MPIr9Ka5j/zZ1JO/PRLtd8iga5fuA2lXNEQKZWBQS4vzM0DCPts8/m0OOCHv/S4HL3GihRPkKr52zeLmtF3TCC304yqRAAAAAWJLR0QAiAUdSAAAAAlwSFlzAAAN1wAADdcBQiibeAAAAAd0SU1FB+EKCQkoIE+WgEEAAAG+SURBVDjLfVLlXwJBEB0UFTswwAAT7EJUxO7uLmyxuxu7Y/9hb3cWbzn9OR923773bnZmbwCEUHl5q318/fw0/vBHBAQGBYeEEoyw33o45SO0kdwRpdSjkY/R6RHExnnq8QnIG4yJPEWSpyGZ0/qUVG4laWCSdXM6ZzMAMjnMys4RMphz8yiZL0FTBHeoPS8p0BBSaKGoiBusykaKS8IR5DO99PdT2GzYcRkz2N20zj+qvKKyKtt9TqmuUdfWkZKfHkz17IOGRjGZOadJWptb2KECa2pV3hjfFttOL7V0oKFToXd106eXQCXvivSIcm8p4/oKoJ/LA4Ma0TDE6WAY5igGrGbB0D+CdCCMIhjTKkoYR34CDAgmlT04phg/DTNsnxU1FVvnKD9vgmK6L6gEXYuzsljvXFqGFQBvyeBcXVvP6NrY1FFla7sXY2fXubcvvaeBHGinsZJDSbcfETlYslByfIIVkVOAs3NBv7ikhqsE1zUnduCqStDJDVZld91ywgV394Le4J7Kxk3OPAAcdsiGYfnPPiJDZ/LpR392yL2nzu29vOqnjHSA3mh122Pqdxv8HY4Pxyf8G1/i4RsJ/fBIGZrsiwAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0xMC0wOVQwOTo0MDozMiswMjowMCPp8zwAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMTAtMDlUMDk6NDA6MzIrMDI6MDBStEuAAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAABJRU5ErkJggg==';
  //img.src= 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';	//empty image

  //methods -----------------------------------------------------------------------------------------------------------
  function log(text) { if (self.debug) { console.log(self.ID + ': ' + text); } }
  function reDrawAnnotatedImg () {
    if(eyeObj.right.set== 0 || eyeObj.left.set== 0)
      return;
    var dX = eyeObj.right.x - eyeObj.left.x; var dY = eyeObj.right.y - eyeObj.left.y;
    var bEyes = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    var alpha = Math.atan2(dY, dX);
    if(betweenEyes / bEyes >100)    //scale too large
      return;
    scale = betweenEyes / bEyes;
    angle = -alpha * 180 / Math.PI;

    var centre = { x: img.width / 2, y: img.height / 2 };
    var pr = rotate(eyeObj.left, centre, -alpha);
    var sx = ((img.width / 2) - pr.x) - (((cnv.width / 2) - fromLeft) / scale);
    var sy = ((img.height / 2) - pr.y) - (((cnv.height / 2) - fromTop) / scale);
    var sh = Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2));
    alpha = Math.atan2(sy, sx);
    shift.x = (sh * Math.cos((angle * Math.PI / 180) - alpha));
    shift.y = (sh * Math.sin((angle * Math.PI / 180) - alpha));

//    console.log('REDRWA ANNOTATED IMG');
//    console.log('ALPHA: '+ angle+ ' SHX: '+ shift.x+' SHY: '+ shift.y);
    
    self.reDraw(0, 0, 0, 0);
  };
  
  function imageToCanvasCoordinatesTranslate(point) {
    var centre={x: (img.width / 2) - shift.x, y: (img.height / 2) + shift.y };      
    var rotated = rotate(point, centre, angle* Math.PI / 180);
    var scaledAndShifted= {x:0, y:0};
    scaledAndShifted.x = (cnvO.width / 2)-  (((img.width / 2) - rotated.x - shift.x) * scale);
    scaledAndShifted.y = (cnvO.height / 2)- (((img.height /2) - rotated.y + shift.y) * scale);
    return scaledAndShifted;
  }

  function reSetImgF() {
    shift.x = 0; shift.y = 0; scale = 1; angle = 0; br = 0; ct = 1; 
    eyeObj.right.set = 0; eyeObj.left.set = 0;
    self.reDraw(0, 0, 0, 0);
  };

  this.resetImg = function () {
    reSetImgF();
  }

  this.setDropCB = function (_obj, _fnc) {
    dropCBObj = _obj;
    dropCB = _fnc;
  }

  this.annotateImg = function (_obj) {
    //XXXX
    //console.log('ANNOTATE CNV RESP: ' + _obj);
    self.disableLock();
    eyeObj.left.x = _obj.left.x;
    eyeObj.left.y = _obj.left.y;
    eyeObj.right.x = _obj.right.x;
    eyeObj.right.y = _obj.right.y;
    
    eyeObj.left.set = _obj.left.set;
    eyeObj.right.set = _obj.right.set;
    reDrawAnnotated();
  }

  this.annotateCnv = function (_obj, imgType) {
    //console.log('ANNOTATE CNV RESP: ' + _obj + 'imgType: ' + imgType);	
	
	if (imgType == "ORIG_IMAGE") {
		// Need to recalculate coordinates to canvas image.		
		_obj.left = imageToCanvasCoordinatesTranslate(_obj.left);
		_obj.right = imageToCanvasCoordinatesTranslate(_obj.right);		
	}	
		
    self.disableLock();
    var centre={x: (cnv.width / 2), y: (cnv.height / 2)};
    var re = rotate(_obj.right, centre, -angle * Math.PI / 180);
    var le = rotate(_obj.left, centre, -angle * Math.PI / 180);

    eyeObj.left.x = ( (img.width / 2) ) -  ( ( ( (cnv.width / 2) - le.x ) / scale ) + shift.x) ;
    eyeObj.left.y = ( (img.height / 2) ) - ( ( ( (cnv.height /2) - le.y ) / scale ) - shift.y) ;
    eyeObj.right.x = ( (img.width / 2) ) -  ( ( ( (cnv.width / 2) - re.x) / scale ) + shift.x) ;
    eyeObj.right.y = ( (img.height / 2) ) - ( ( ( (cnv.height /2) - re.y) / scale ) - shift.y) ;
	
    eyeObj.left.set = _obj.left.set;
    eyeObj.right.set = _obj.right.set;
    reDrawAnnotated();  //was withImg at the end
  };
  this.setAnnotateCnvReq = function (_ar) {annotateCnvReq = _ar; };
  function annotateReq() {
    if(typeof(annotateCnvReq) == 'function') {
      self.enableLock();
      var ximg= cnv.toDataURL('image/png').split(',');
      annotateCnvReq(self.ID, ximg[1]); //send canvas for annotation
    }
  };
  this.getCanvasData = function () {
    var ximg= cnv.toDataURL('image/png').split(',');
    return( ximg[1] );
  };

  this.getImgData = function () {
    var cnhelper = document.createElement("canvas");
    cnhelper.width = img.width;
    cnhelper.height = img.height;
    var ctxhelper = cnhelper.getContext('2d');
    ctxhelper.drawImage(img, 0, 0);
    ctxhelper.restore();
    var ximg= cnhelper.toDataURL('image/png').split(',');
    return( ximg[1] );
  };

  this.loadImg = function (_imgsrc) { img.src = _imgsrc; };
  this.setUpdateCallBack = function (_cb) { updateCallBack = _cb; };
  this.enableGS = function () { gs = 1; self.reDraw(0, 0, 0, 0); };
  this.disableGS = function () { gs = 0; self.reDraw(0, 0, 0, 0); };
  this.enableEyeLines = function () { eyeLines = 1; self.reDraw(0, 0, 0, 0); };
  this.disableEyeLines = function () { eyeLines = 0; self.reDraw(0, 0, 0, 0); };
  this.enableDrop = function () { cnvO.addEventListener('drop', drop, false); };
  this.disableDrop = function () { cnvO.removeEventListener('drop', drop, false); };
  this.enableSync = function () { self.sync = 1; };
  this.disableSync = function () { self.sync = 0; };
  this.enableLock = function () { self.lock = 1; rSlider.disabled = true; brSlider.disabled = true; ctSlider.disabled = true; };
  this.disableLock = function () { self.lock = 0; rSlider.disabled = false; brSlider.disabled = false; ctSlider.disabled = false; };
  this.enableAnnotation = function () {
    if(annotationEnabled == 1)
      return (-1);
    annotationEnabled = 1;
    annotAImg.addEventListener('click', annotateReq, false);
    annotLImg.addEventListener('click', setLeftEye, false);
    annotRImg.addEventListener('click', setRightEye, false);  
    reDrawImg.addEventListener('click', reDrawAnnotated, false);
    reSetImg.addEventListener('click', reSetImgF, false);
    elem.appendChild(annotAImg);
    elem.appendChild(annotLImg);
    elem.appendChild(annotRImg);
    elem.appendChild(reDrawImg);
    elem.appendChild(reSetImg); 
    //console.log('ENABLE ANNOTATION ' + self.ID); 
  };
  this.disableAnnotation = function () {
    if(annotationEnabled == 0)
      return (-1);
    annotationEnabled = 0;
    annotAImg.removeEventListener('click', annotateReq, false);
    annotLImg.removeEventListener('click', setLeftEye, false);
    annotRImg.removeEventListener('click', setRightEye, false);  
    reDrawImg.removeEventListener('click', reDrawAnnotated, false);
    reSetImg.removeEventListener('click', reSetImgF, false);
    elem.removeChild(annotAImg);
    elem.removeChild(annotLImg);
    elem.removeChild(annotRImg);
    elem.removeChild(reDrawImg);
    elem.removeChild(reSetImg); 
    //console.log('DISABLE ANNOTATION ' + self.ID); 
  };

  this.init = function (obj) {
    x = obj.x; y = obj.y; w = obj.w; h = obj.h;
    cnv.width = w;
    cnvO.width = w;
    cnv.height = h;
    cnvO.height = h;
    cnv.style.zIndex = 0;
    cnvO.style.zIndex = 1;
    cnv.style.left = x.toString() + 'px';
    cnvO.style.left = x.toString() + 'px';
    cnv.style.top = y.toString() + 'px';
    cnvO.style.top = y.toString() + 'px';
    //cnv.style.border= '10px solid #006AA3';
    cnv.style.border = '1px solid #006AA3';
    cnvO.style.border = '1px solid #006AA3';
    cnv.style.borderRadius = ' 10px';
    cnvO.style.borderRadius = ' 10px';

    rect = cnv.getBoundingClientRect();
    sizeTemp = Math.sqrt(Math.pow(cnv.width, 2) + Math.pow(cnv.height, 2));
    cnvTemp.height = sizeTemp; cnvTemp.width = sizeTemp;
    t.x = cnvTemp.width / 2; t.y = cnvTemp.height / 2;
    cOffset.x = (cnv.width - cnvTemp.width) / 2;
    cOffset.y = (cnv.height - cnvTemp.height) / 2;
    cOffsetTmp = { x: 0, y: 0 };
    betweenEyes = cnv.width / 4;
    fromLeft = (cnv.width - betweenEyes) / 2;
    fromTop = cnv.height / 2.5;

    rImg.style.left = (x).toString() + 'px';
    brImg.style.left = (x).toString() + 'px';
    ctImg.style.left = (x).toString() + 'px';

    rImg.style.width = '20px';
    rImg.style.height = '20px';
    brImg.style.width = '20px';
    brImg.style.height = '20px';
    ctImg.style.width = '20px';
    ctImg.style.height = '20px';


    annotAImg.style.left = (x+ 0).toString() + 'px';
    annotLImg.style.left = (x+ 50).toString() + 'px';
    annotRImg.style.left = (x+ 100).toString() + 'px';
    reDrawImg.style.left = (x+ 150).toString() + 'px';
    reSetImg.style.left = (x+ 200).toString() + 'px';

    rImg.style.top = (y + cnv.height + 30).toString() + 'px';
    brImg.style.top = (y + cnv.height + 60).toString() + 'px';
    ctImg.style.top = (y + cnv.height + 90).toString() + 'px';

    annotAImg.style.top = (y + cnv.height + 130).toString() + 'px';
    annotLImg.style.top = (y + cnv.height + 130).toString() + 'px';
    annotRImg.style.top = (y + cnv.height + 130).toString() + 'px';
    reDrawImg.style.top = (y + cnv.height + 130).toString() + 'px';
    reSetImg.style.top = (y + cnv.height + 130).toString() + 'px';

    annotAImg.style.width = '32px';
    annotLImg.style.width = '32px';
    annotRImg.style.width = '32px';
    reDrawImg.style.width = '32px';
    reSetImg.style.width = '32px';

    annotAImg.style.height = '32px';
    annotLImg.style.height = '32px';
    annotRImg.style.height = '32px';
    reDrawImg.style.height = '32px';
    reSetImg.style.height = '32px';

    rSlider.style.width = (cnv.width - 40).toString() + 'px';
    brSlider.style.width = (cnv.width - 40).toString() + 'px';
    ctSlider.style.width = (cnv.width - 40).toString() + 'px';
    rSlider.style.left = (x + 40).toString() + 'px';
    brSlider.style.left = (x + 40).toString() + 'px';
    ctSlider.style.left = (x + 40).toString() + 'px';
    rSlider.style.top = (y + cnv.height + 30).toString() + 'px';
    brSlider.style.top = (y + cnv.height + 60).toString() + 'px';
    ctSlider.style.top = (y + cnv.height + 90).toString() + 'px';
    //console.log('INIT DONE !!!');
    onLoad();
  };
  function onLoad() {
    log('---------------------------');
    log('VER:    ' + self.version);
    log('ID:     ' + self.ID);
    log('Img.WH: ' + img.width + ', ' + img.height); 
    log('Scale : ' + scale + '\n');
    cOffsetTmp.x = ((cnvTemp.width - img.width) / 2);
    cOffsetTmp.y = ((cnvTemp.height - img.height) / 2);

    self.reDraw(0, 0, 0, 0);
//    console.log('ONLOAD  DONE!!!');
  }
  this.reDraw = function (dS, dX, dY, dR) {
    scale *= Math.pow(1.0005, dS);
    dX = dX / scale; 
    dY = dY / scale;
    var sh = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    var alpha = Math.atan2(dY, dX);
    shift.x += (sh * Math.cos( (angle * Math.PI / 180) - alpha));
    shift.y += (sh * Math.sin( (angle * Math.PI / 180) - alpha));

    if ( angle + dR >= -180 && angle + dR <= 180 )
      angle += dR;
    ctxTemp.clearRect(0, 0, cnvTemp.width, cnvTemp.height);           //clear ctxTemp
    ctx.clearRect(0, 0, cnv.width, cnv.height);                       //clear ctx
    ctxO.clearRect(0, 0, cnvO.width, cnvO.height);                       //clear ctx
    ctxTemp.save();
    ctxTemp.translate(t.x, t.y);
    ctxTemp.rotate(angle * Math.PI / 180);                             //rotate ctx
    ctxTemp.scale(scale, scale);
    ctxTemp.drawImage(img, cOffsetTmp.x + shift.x - t.x, cOffsetTmp.y - shift.y - t.y);  //place image
    ctxTemp.restore();
    ctx.drawImage(cnvTemp, cOffset.x, cOffset.y);

    var imgData = ctxTemp.getImageData(0, 0, cnvTemp.width, cnvTemp.height);
    var res = imgFilter(imgData, br, ct, gs);
    ctx.putImageData(res, cOffset.x, cOffset.y);  //draws up the image.

		if (typeof(updateCallBack) == 'function')
      updateCallBack( ctx.getImageData(0, 0, cnv.width, cnv.height) );

    if (eyeLines) {
      var alpha = Math.atan2(shift.y, shift.x);
      var a = (angle * Math.PI / 180) - alpha;
      var sh = Math.sqrt(Math.pow(shift.x, 2) + Math.pow(shift.y, 2)) * scale;
      var xs = (cnvO.width / 2) +  (sh * Math.cos(a));
      var ys = (cnvO.height / 2) + (sh * Math.sin(a));
  
      ctxO.beginPath();                                                        //red line offset
      ctxO.strokeStyle = '#ff0000';
      ctxO.moveTo((cnv.width / 2), (cnv.height / 2));
      ctxO.lineTo(xs, ys);
      ctxO.stroke();

      ctxO.beginPath();                                                        //eyes
      ctxO.strokeStyle = '#00ff00';
      ctxO.setLineDash([5, 10]);
      ctxO.moveTo(fromLeft,0); ctxO.lineTo(fromLeft, cnvO.height);
      ctxO.moveTo(fromLeft + betweenEyes, 0); ctxO.lineTo(fromLeft + betweenEyes, cnvO.height);
      ctxO.moveTo(0, fromTop); ctxO.lineTo(cnvO.width, fromTop);
      ctxO.stroke();
      ctxO.beginPath();                                                        //center
      ctxO.strokeStyle = '#0000ff';
      ctxO.setLineDash([100, 0]);
//      ctxO.moveTo(cnv.width/ 2, 0); ctx.lineTo(cnv.width/ 2, cnv.height);    //cros +
//      ctxO.moveTo(0, cnv.height/ 2); ctx.lineTo(cnv.width, cnv.height/ 2);
      ctxO.moveTo(0, 0); ctxO.lineTo(cnvO.width, cnvO.height);                    //cross X
      ctxO.moveTo(cnvO.width, 0); ctxO.lineTo(0, cnvO.height);
      ctxO.stroke();

      var centre={x: (img.width / 2) - shift.x, y: (img.height / 2) + shift.y };      
      if(eyeObj.left.set !== 0) {
        var prL = rotate(eyeObj.left, centre, angle* Math.PI / 180);
        var eXL = (cnvO.width / 2)-  (((img.width / 2) - prL.x - shift.x) * scale);
        var eYL = (cnvO.height / 2)- (((img.height /2) - prL.y + shift.y) * scale);
        ctxO.strokeStyle = '#ff0000';
        ctxO.setLineDash([100, 0]);
        ctxO.lineWidth = 4;
        ctxO.beginPath();                                                        //eyes
        ctxO.moveTo(eXL - 20, eYL - 20);
        ctxO.lineTo(eXL + 20, eYL + 20);
        ctxO.moveTo(eXL - 20, eYL + 20);
        ctxO.lineTo(eXL + 20, eYL - 20);
        ctxO.stroke();
        ctxO.closePath();
        ctxO.lineWidth = 1;
      }
      if (eyeObj.right.set !== 0) {
        var prR = rotate(eyeObj.right, centre, angle * Math.PI / 180);
        var eXR = (cnv.width / 2)-  (((img.width / 2) - prR.x - shift.x) * scale);
        var eYR = (cnv.height / 2)- (((img.height /2) - prR.y + shift.y) * scale);
        ctxO.strokeStyle = '#ff0000';
        ctxO.setLineDash([100, 0]);
        ctxO.lineWidth = 4;
        ctxO.beginPath();                                                        //eyes
        ctxO.moveTo(eXR - 20, eYR - 20);
        ctxO.lineTo(eXR + 20, eYR + 20);
        ctxO.moveTo(eXR - 20, eYR + 20);
        ctxO.lineTo(eXR + 20, eYR - 20);
        ctxO.stroke();
        ctxO.closePath();
        ctxO.lineWidth = 1;
      }
    }
    imgCoord.x = (img.width / 2) - shift.x;
    imgCoord.y = (img.height / 2) + shift.y;
    rSlider.value = angle;
    brSlider.value = br * 100 / 255;
    ctSlider.value = (ct - 1) * 100;

    //self.debug = 1;
    log('---------------------------');
    log('Scale:  ' + scale);
    log('Angle:  ' + angle);
    log('ShiftX: ' + shift.x);
    log('ShiftY: ' + shift.y);
    log('BR:     ' + br);
    log('CT:     ' + ct);
  }
  function update(dS, dX, dY, dR) {
    if (self.check(dS, dX, dY, dR) != 0) return (-1);
    if (self.sync) {
      if (self.neighbor.check(dS, dX, dY, dR) != 0)
        return (-1);
      self.neighbor.reDraw(dS, dX, dY, dR);
    }
    self.reDraw(dS, dX, dY, dR);
  }
  this.check = function(dS, dX, dY, dR) {  //check if redraw is possible due to imits
    if (self.lock != 0) return (-1);
    return (0);  //TODO check if possible
  };
  function setLeftEye() {
    if (eyeObj.left.set !== 0) {
      if(eyeObj.right.x == imgCoord.x && eyeObj.right.y == imgCoord.y)
        return (-1);
    }
    eyeObj.left.x = imgCoord.x; eyeObj.left.y = imgCoord.y; 
    eyeObj.left.set = 1;
    self.reDraw(0, 0, 0, 0);
  }
  function setRightEye() {
    if (eyeObj.right.set !== 0) {
      if (eyeObj.left.x == imgCoord.x && eyeObj.left.y == imgCoord.y)
        return (-1);
    }
    eyeObj.right.x = imgCoord.x; eyeObj.right.y = imgCoord.y;
    eyeObj.right.set = 1;
    self.reDraw(0, 0, 0, 0);
  }
  function reDrawAnnotated() { reDrawAnnotatedImg(); }  //was with param eyeObj
  function mouseMove(evt) {
    if(mouse.down !== 0) {
      var x = evt.clientX - rect.left; var y = evt.clientY - rect.top;
      //console.log('MX: '+ x+ ' MY: '+ y);
      var dx = x- mouse.lastX; var dy = y- mouse.lastY;
      mouse.lastX = x; mouse.lastY = y;
      update(0, dx, dy, 0);
    }
  }
  function mouseWheel(e) {
    e.preventDefault();
    var delta = e.wheelDelta ? e.wheelDelta : (-10* e.detail);
    if(mouse.pressed == 17) { update(0, 0, 0, +delta / 10); return; }
    if(mouse.pressed == 90) { update(0, 0, +delta / 10, 0); return; }
    if(mouse.pressed == 88) { update(0, +delta / 10, 0, 0); return; }
    update(+delta, 0, 0, 0);
  }
  function keyDown(evt) { mouse.pressed = evt.keyCode; }
  function keyUp(evt) { mouse.pressed = 0; }

  function drop(evt) {
    evt.preventDefault(); evt.stopPropagation();
    var files = evt.dataTransfer.files;
    if (files[0].type.indexOf('image/') === 0) {
      log('DRAG Loading image file: ' + files[0].name);
      fReader.readAsDataURL(files[0]);
      reSetImgF();
      if(typeof(dropCBObj.dropCB) == 'function') {
        dropCBObj.dropCB();
      }
      //TODO: scale it to fit canvas?
    } else {
      log('ERROR: DRAG File is not an image: ' + files[0].name);
    }
  }
}

//Helper functions -----------------------------------------------------------
function imgFilter(imgData, br, ct, gs) {
  var d = imgData.data;
  var len = d.length;
  var i;
  for (i = 0; i < len; i += 4) {
    d[i] += br;  //bright
    d[i+ 1] += br;
    d[i+ 2] += br;

    d[i] *= ct; //contrast
    d[i+ 1] *= ct; //G
    d[i+ 2] *= ct; //B
  }
  if (gs !== 0) {
    for (i = 0; i < len; i += 4) {
      var res = 0.2126 * d[i] + 0.7152 * d[i + 1] + 0.0722 * d[i + 2];
      d[i]    = res; 
      d[i + 1] = res;
      d[i + 2] = res;
    }
  }
  return (imgData);
}

function rotate(p, c, a) {																			//rotate point p around center c for angle a
  var pr = { x: (((p.x - c.x) * Math.cos(a)) - ((p.y - c.y) * Math.sin(a)) + c.x) | 0,
            y: (((p.x - c.x) * Math.sin(a)) + ((p.y - c.y) * Math.cos(a)) + c.y) | 0} ;
  return (pr);
}
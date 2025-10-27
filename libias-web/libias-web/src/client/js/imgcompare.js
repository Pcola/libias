/*
TODO:
*/

'use strict';
function ImgCompare(_elem, left, top, right, bottom, bottomSpacing, isSucher) {  
  function log(text) { if (self.debug) { console.log(self.ID + ': ' + text); } }
  var elem = _elem;
  var self = this;
  var synced = 0, gs = 0, lines = 0;
  var l =  { x: 0, y: 0, w: 0, h: 0 };
  var r =  { x: 0, y: 0, w: 0, h: 0 };
  var cmp = { x: 0, y: 0, w: 0, h: 0 };
  var verifyReq = null;
  var manualSync = 0;
  self.version = '0.0.1';
  self.debug = 0;
  self.ID = 'C';
  var prevZoomRatio = -1;
  var zoomRatio = window.devicePixelRatio;
  if (zoomRatio >= 1) {
	// We want to handle only lower sized zooms on init phase.
	// This ensures that if the user starts another window with for
	// example 50% zoom and goes to this window, then the window.innerWidth
	// is recalculated also too. The uppser we don't want to recalculate
	// because in the first time we want to show all gui on the screen.
	zoomRatio = 1;  
  }
  bottom = bottom * zoomRatio;

  if(1) {     //add css for sliders, images and canvas
    var style = '\
    .jsman {\
      position: absolute;\
      -moz-user-select: none;\
      -webkit-user-select: none;\
      -ms-user-select: none;\
      user-select: none;\
      -webkit-user-drag: none;\
      -user-drag: none;\
      -webkit-touch-callout: none;\
    }\
    img.jsman { border: 2.5px solid transparent;}\
    img.jsman:focus { border: 2.5px solid #006aa3; }\
    input[type=range].jsman_slider {\
    -webkit-appearance: none; width: 100%; margin: -1.05px 0; padding: 0px; position: absolute;}\
    input[type=range].slider:focus {\
    outline: none;}\
    input[type=range].jsman_slider::-webkit-slider-runnable-track {\
      width: 100%; height: 20.1px; cursor: pointer; box-shadow: 0px 0px 1px #000000, 0px 0px 0px #0d0d0d;\
      background: #006aa3; border-radius: 0px; border: 0px solid #010101;}\
    input[type=range].jsman_slider::-webkit-slider-thumb {\
      box-shadow: 0px 0px 1px #670000, 0px 0px 0px #810000;\
      background: rgba(155, 215, 225, 0.93); border-radius: 50px; border: 0px solid #ff1e00;\
      height: 18px; width: 10px;\
      cursor: pointer;\
      -webkit-appearance: none;\
      margin-top: 1.05px;\
    }\
    input[type=range].jsman_slider:focus::-webkit-slider-runnable-track {\
    background: #006aa3;}\
    input[type=range].jsman_slider::-moz-range-track {\
      box-shadow: 0px 0px 1px #000000, 0px 0px 0px #0d0d0d;\
      background: #006aa3; border-radius: 0px; border: 0px solid #010101;\
      width: 100%; height: 20.1px;\
      cursor: pointer;\
    }\
    input[type=range].jsman_slider::-moz-range-thumb {\
      box-shadow: 0px 0px 1px #670000, 0px 0px 0px #810000;\
      background: rgba(255, 255, 255, 0.93); border-radius: 50px; border: 0px solid #ff1e00;\
      height: 18px; width: 10px;\
      cursor: pointer;\
    }\
    input[type=range].jsman_slider::-ms-track {\
      width: 100%; height: 20.1px;\
      cursor: pointer;\
      background: transparent; border-color: transparent;\
      color: transparent;\
    }\
    input[type=range].jsman_slider::-ms-fill-lower {\
      background: #006aa3; border-radius: 0px; border: 1px solid transparent;\
      box-shadow: 0px 0px 1px #000000, 0px 0px 0px #0d0d0d;\
    }\
    input[type=range].jsman_slider::-ms-fill-upper {\
      background: #006aa3; border-radius: 0px; border: 1px solid transparent;\
      box-shadow: 0px 0px 1px #000000, 0px 0px 0px #0d0d0d;\
    }\
    input[type=range].jsman_slider::-ms-thumb {\
      box-shadow: 0px 0px 1px #670000, 0px 0px 0px #810000;\
      background: rgba(255, 255, 255, 0.93); border-radius: 50px; border: 0px solid #ff1e00;\
      width: 10px; height: 18px;\
      cursor: pointer;\
    }\
    input[type=range].jsman_slider:focus::-ms-thumb {\
      box-shadow: 0px 0px 1px #670000, 0px 0px 0px #810000;\
      background: rgba(255, 255, 255, 0.93); border-radius: 50px; border: 0px solid #ff1e00;\
      width: 10px; height: 18px;\
      cursor: pointer;\
    }\
    input[type=range].jsman_slider:focus::-ms-fill-lower {background: #8cc8ff; border: 2px solid #006aa3}\
    input[type=range].jsman_slider:focus::-ms-fill-upper {background: #8cc8ff; border: 2px solid #006aa3}';
    var jsman_css = document.createElement('style');
    jsman_css.type = 'text/css';
    jsman_css.innerHTML= style;
    elem.appendChild(jsman_css);
  }
  var imgInfo = new ImgInfo(elem);
  var imgCmp = new ImgCmp(elem);
  var imgMan = [new ImgMan(0, elem), new ImgMan(1, elem)];
  imgMan[0].setDropCB(self, self.dropCB);
  imgMan[1].setDropCB(self, self.dropCB);
  var lckImg = document.createElement('img');
  var gsImg = document.createElement('img');
  var lnImg = document.createElement('img');
  var verify = document.createElement('img');
  var verifyO = document.createElement('img');
  var score = document.createElement('div');

  var iUnlock = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAA6lBMVEUAAAAAbf8AbPEAbfAAbPAAbfAAbfAAcPUAbvAAbfAAYP8AbPAAbfAAavQAbPAAbO4AbfAAbfAAbfAAbfEAb+4AbfEAbO8AbfAAbfAAbe8Aa/IAbfAAbvAAc/IAcfEAbe8AbfEAbfAAbfAAbvEAbfEAbfAAbPEAAP8AbPAAbPAAbfAAcO8AdOgAbu8AbfAAbe8AbPAAbOsAcO8AgP8Aa+0AbvAAbfAAbfAAbvAAbfAAbfAAbfAAbfAAbfAAa/IAb/IAbvAAdusAbfAAbe8Abu8AbPIAbfAAb/QAbfAAbfEAcfAAbfEAbfAAAADuozsEAAAATHRSTlMAB0mYwdrzGZfyCIT1GMgt4/6WSx44b+ftPxO5VhQSxLL910psjH8BNGPQMAtf2GB2GhAGK6y7qKq4+YjgZzknhw3ppIAovRf6fiI2iDW4iQAAAAFiS0dEAIgFHUgAAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfhCRgPIwA8hE/aAAABP0lEQVQ4y4WTh1LDMAyG/wynmW0ZZRdK2ZQ9C2WvFvD7Pw+yEyexGy7/XU6J9MWWJRnIZdmOyzyPuY5tYVoNP+C5Ar9hhMMo5priKCzHkyafUjMp4q02r1C7lf9fGSciWyM01p+ZnZvvLCyKXdI8Ij2+tCy9K6v0HsnzGfmvka9LzzqtEYvT+np8g3Lqbfa3gG368ql+gQ7sALtk2B72RcUs2EbqB8ChsAMcCWPDMYBj4ETYDkJhHLgGcKoDLlh2+LPzC6lL4OqaHDe3Q+Fn8FLgrtya7n2+oKeAkdbchxLAysDj8MkAmEpSAs+c9w3AVcd8Ef7XN/5uAI4qVLrFiOqjA7YqdQp88E8doFJnzfpKR2cg53k8UYBftPv7J1H67am4bLc5MGVFlSNXKBu52qGtH/v6i1N/9eov7//X/w8vc66uqFs5oQAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0wOS0yNFQxNTozNTowMCswMjowMJQMF7kAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMDktMjRUMTU6MzU6MDArMDI6MDDlUa8FAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAABJRU5ErkJggg==';
  var iLock = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAn1BMVEUAAAAAbf8AbPEAbfAAbPAAbfAAbfAAcPUAbvAAbfAAYP8AbPAAbfAAavQAbPAAbO4AbfAAbfAAbu8Abe8AbfEAavEAbfAAbe4AbPAAbe8AbfAAAP8AafAAbfAAbfEAbfAAbfAAbfEAbfAAbu8Ab/EAbPAAbPEAbPIAbfEAa/IAbO8AbPEAbu8AbfAAbfAAbe4AbPIAbfAAbe0AbfAAAABsJ6uYAAAAM3RSTlMAB0mYwdrzGZfyCIT1GMgt4/azgcMk1z00ou8BEbxur4iw/HI3aH07OBOSf0/g2E0ouA4RwP8sAAAAAWJLR0QAiAUdSAAAAAlwSFlzAAAN1wAADdcBQiibeAAAAAd0SU1FB+EJGA8iMANGTjcAAAEpSURBVDjLhVPXtoJADBxgQQREsfdrwd7d//83WYSERe5hXhKSIdk0gGCYlrAdxxaWaeAXDbcpCU23UXJ7fiA1BL5X9LdC+YOwxf52R1ag06b/K/0JI4vhcfyo2+t1I87yfYdPhv5AfQ/6ZPDT+uj9w+zh3pBqUdW6xB8B48lkDIzI5Cb9o/5MZ5gn+aM5ZlPqmAGT2AtgqeQSWJDRhEX6H7BScgWsyWhBkL6uIgjYubrZAvEuQQxsN7nVhpOr++Jw9rnVYcKhSDgwgVIcleN0Pp+UPHIKoREuUl40guAyY+W4SnnVCBY3Ko2A2w0aweRWfwn3u0ZIWk3DeqSe5zMVDx4Wjfv15irfr8K4aWGCkJCviF9euRKylatd2vq1rz+c+tOrP97/z/8DT512WZPqADQAAAAldEVYdGRhdGU6Y3JlYXRlADIwMTctMDktMjRUMTU6MzQ6NDgrMDI6MDDMazwaAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE3LTA5LTI0VDE1OjM0OjQ4KzAyOjAwvTaEpgAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAASUVORK5CYII=';
  var iBW = 'data:image/png;base64,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';
  var iBWC = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QAjAA8ADyPS4afAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH4QkYDjgebn/TFAAABRpJREFUaN7lmk9oHFUcxz+z2V03yaZVTFNoUYq9SBqoHuxBFEuLoqJCc9NePNtTDj2YXDwpouhBL54EbahCcyrk4qVgFWzB6CE3C6GgwkYsaTab7L/5edjv2307Tv7sZmZTceAx8+b93m9+/9/7/d4EJHLZceAscAY4BZwAjgAjAqgAq8AKsAzcAm5A8DsHd9k42AzYbbAmmKmF3r2mFnrv3VhTc2fAHu2XiqAPwo8Bs8DbnoQ3WhLlJvArcAcoAVsaLwATwEngNPCcNDbqaehL4AMI/khL4nmwObCypNgEWwSbBiv0ga+guYueBstgs61vJUv8JNiSp/5rYFN7nBuADe0CMwW24JnaUuubyRA/DXZfiH8DO9fj/B/AKmB/gn0P9gnYK2C5GNjz+kaob17YL/GXwBqS/BWwYh84os7tnlfBPgJ7LAJfBJsXXKNFQ//EO5W+uw8hGFgV7Amw16SNqsfQJtjHYIci82a971/qx2wamvzOPrUYCteGnHSkZT5t4pq63wV7cRshNnowJ5uU/dn+JN+lgaYk7zNQVf8FsJ89uPdajt+lCRNNk3sJlUvi+kpCQcCZ0LCIL6uf8/oVsPc9rX8FlvVwzHvRaacQa3NetCkmxEAogoc8yYcac33H0BtiKAS7CpbxHNtFp9kdVtj25HMJriHONGodQrsYy3UzZs+DrWvep5EQG4rGY3Ef+lyTriW8CFrEiUcjPjCq5pvW694e6k0P14LwfRa3MStLUlMJMxB6PjAsRmoidFRhdNNjrCaYy5r7t3a7bsVuilZ/A2gzAl5MYQ/lnPghtQ2wunxi2GNgWO/qgimAfaf5Vz18i6J1xv/Ibb2cTokBF0Y3JOm8mFr3NLMOtqWxUU9TVc1/ylujwhbN7WTEmkJQSJGBhqSbi2hmqBUyu6JTXrB1sC86W5n2LnZdOI8DdlEcXU9pG95DGN0xOlXAHhbMdb27mFEaGCgZSeNyq2perd5qluvuk9c7r98eD5UUvSpcN4X3TEY5LMqk0rqywD3gL+AoUFQWVlLufETPGxo7KlgHPyc8ZyO0nsoqATelganYUKQfqvl9X1tx4z/pWY7MHeE9AdiaHOJwSvRbZB2IizZ+dNqKiU7OD0rCeVj9tawS86aXgKd11SThPJCDoCbl5DvjQV2btSACn5fED2l/tAU0gJHsIOovwBCwqeeiCA3loK6yUW45cZCRM1f0vqh5te2cqyKgAlBNMQrVRUhOknfRJueNK7SS6/TJAY/o+T4EodarLLCeVcXskOo2aylpoQmMi5mSiCrqLrtmQkSXdZ/Q+5JnZisebACsZvTSVHQaRBTq51JewC+6nxTelYxqlYEqZmktZEOK6SVJryhJu/6Enssac303/qNw3dD9tPAuZ1RoReU+UmTCs+2g3olGXb6Q05jX53HgWUWexQittwaxmUtqL7TdZm4g2+ndkvq4asUI2JNe2eXpbbbTA0lomjF1IZ/Q7Rira/43uyU0aaeUjT4YcKZzbw8pZVdSv/AAmFDVqxG9tYek/l9llfMH7MSu9VJWaRe2LOHCVq91oXLH7tuFrTHRZDsUtlItLe7mA2Ni0JXev+6ztJhacXc3H6glVNxNrbwex8AzYN969n4X7KUEyuupHHC4ulAF7EOw5ZQPOGKPmOYHdMQ0ltARUxvhhcghX48hNvaQ7+UBHfJ1ObZ/zLrwHzpm7QqxD9xB9//pVwPizhSS+tljfL8Vg/0yc2C/2/wDJjhDokeVPs8AAAAASUVORK5CYII=';
  var iLines = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAANlBMVEUAAAAAbfEAbfAAbe4AbfAAa+8AbfAAbfAAcfEAbfAAbPEAbvAAbe8AYP8AbPEAcPMAbfAAAADpI2IhAAAAEXRSTlMAn+BNiB+4wBKYoGSDCH8p2B5JDYAAAAABYktHRACIBR1IAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4QkYEQQgy5dTDQAAAEtJREFUOMvt0ykCACAIBEAUDzzx/681WJRA1eBUNixhwViVAXQqhG/nQxSCPwKUspDodunHlKwqUKOq3n7gMa3L1fZ2BHjI3Q9elwmaxQUFU0HeaQAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0wOS0yNFQxNzowNDozMiswMjowMJtguQcAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMDktMjRUMTc6MDQ6MzIrMDI6MDDqPQG7AAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAABJRU5ErkJggg==';
  var compare = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAABgFBMVEUAAAAAcO8Abe8AbfEAbfAAbfAAbfEAbfEAb/QAbfAAbfAAbfAAbe8AbfAAbfAAbPAAbu8AbfAAbfAAbvEAdOgAauoAbu8AbfAAbfAAbvEAZuYAbfAAbfAAbO8Abu8AbfAAbfAAceMAbvAAbfAAbfEAbvEAbfEAbvAAbfAAbfAAbe0AcfEAbfAAbe8AbfAAbfAAbfAAbfAAbfAAbfAAbfAAbfAAbfAAbe8AbOsAbvAAb+8AbvAAbPAAbfAAbPAAbe8AbfAAbfAAb+4AAP8Abe8AbfAAcfAAVf8Abu4AbfMAgP8AbPAAbO8Abf8AbPEAbfAAcO8AbvEAYP8AbPIAbvEAbu8AbfAAbe8AcPMAbfEAbvEAbfAAbPAAgP8AbfAAbfAAbfAAbe8Abe8Aa/IAbO8AbvAAbfAAbfAAbfAAbPAAbO4AbvAAc/IAbPEAbPAAbfAAbfAAbfMAbfAAbe8AbPAAgP8Aa/IAbfEAbfAAbfAAbPAAbfEAb+4AZv8AbfAAAADnbC3cAAAAfnRSTlMAIIHU8+KfWxdE9PXV2fpCT/jvawsMcu73Sgrt+UBB++sJZIqPSEuczPwOEv7F2+nw7NDN8fLLtBp7PkM06mgxwL4uAWC4IgMsKgIhYQdayTBdCCiQsyORKY2O01cG/dHSUnBMkjN6jPaZLZcUf9abmhVl5rUEObHH3sh8PAWwzpw3AAAAAWJLR0QAiAUdSAAAAAlwSFlzAAAN1wAADdcBQiibeAAAAAd0SU1FB+ELDwspBQYGx0UAAAGFSURBVDjLdZJnW8IwFEYjYNWCo4obFHEgFVRw4AIHFcGJAwVFxQHuvdf729XSAuLlfslJ35ObJ0kZy6kijVZXzJWUsgJVxkNvKK8AX0nnVUJ1jZGx2rr6hkYqb2o2mdPU0mppIwQr2lXsgJUQOHSq2AUbIXTbRRV7HE5C6O3Lcr+BELSuHMFNCAODWR4aJgQPRlQcgYcQRlGVuTKMEsJYdpkHY9RVOjGehnFQp2RswuHtkq/J65ggBeYT4OY4NwQfK1CTU3qAn56hU780G2BzwdA8W5hd9P+Ll5ZXIISVRgK6V9f+xOsbzUBkU51uRoD6rWgmNsbscG2HcleEtl2wx4zKzIad+G7+nrtxg/pXBPb2/8W/tZbYC8hwgEP6XIc4UF7piBaOlDc7RpIWkjiWxxTMtGBGSh4lxEQqF08gyRA8BX8Wzo/PL3Q4Dab58soCXN8sRpVGYlS61QIuzV1Gb7uPDOGnTA+JxIPpl3SPvqa/LZ+eU7cvr288//b+cvPx+aV+/wY3BE/A+t/AlgAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0xMS0xNVQxMTo0MTowNSswMTowMKXcpEkAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMTEtMTVUMTE6NDE6MDUrMDE6MDDUgRz1AAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAABJRU5ErkJggg==';

  //verify.src = 'avatar.png';

  verify.title= 'Vergleichen';
  verifyO.title= 'Vergleichen (Originale)';

  lckImg.title = 'Bilder synchron/nicht synchron halten';
  gsImg.title = 'Umschalten auf S/W';
  lnImg.title = 'Hilfslinien';

  verify.src = compare;
  verifyO.src = compare;
  lckImg.src = iUnlock;
  gsImg.src = iBWC;
  lnImg.src = iLines; 

  lckImg.addEventListener('click', toggleSync, false);
  gsImg.addEventListener('click', toggleGS, false);
  lnImg.addEventListener('click', toggleEL, false);
  verify.addEventListener('click', verifyE, false);
  verifyO.addEventListener('click', verifyOE, false);

  score.className = 'jsman';
  lckImg.className = 'jsman';
  gsImg.className = 'jsman';
  lnImg.className = 'jsman';
  verify.className = 'jsman';
  verifyO.className = 'jsman';

  lckImg.tabIndex = 0;
  gsImg.tabIndex = 0;
  lnImg.tabIndex = 0;
  verify.tabIndex = 0;
  verifyO.tabIndex = 0;

  elem.appendChild(lckImg);
  elem.appendChild(gsImg);
  elem.appendChild(lnImg);
  elem.appendChild(verify);
  elem.appendChild(verifyO);
  elem.appendChild(score);

  imgMan[0].neighbor = imgMan[1]; imgMan[1].neighbor = imgMan[0];
  imgMan[0].setUpdateCallBack(imgCmp.loadFace1); imgMan[1].setUpdateCallBack(imgCmp.loadFace2);

  reCalc(false, isSucher);

  var doc = document;
  var cWidth = window.innerWidth;
  var newWidth = cWidth;
  window.onresize = function(event) {
	  newWidth = window.innerWidth;
	  if(cWidth != newWidth){
		// Some browser like I calls twice onResize. With this feature is possible
		// to filter the redundant one call.
        cWidth = newWidth;
        detectBrowserWindowChange();
		reCalc(true, isSucher);	  
	  };	  
  };

//  this.reDrawAnnotatedImg = function(_ID, _obj) { imgMan[_ID].reDrawAnnotatedImg(_obj);}  //171120 removed
  this.annotateCnv = function(_ID, _obj, _imgType) { imgMan[_ID].annotateCnv(_obj, _imgType); }
  this.annotateImg = function(_ID, _obj) { imgMan[_ID].annotateImg(_obj); }
  this.enableAnnotation = function(_ID) { imgMan[_ID].enableAnnotation(); }
  this.disableAnnotation = function(_ID) { imgMan[_ID].disableAnnotation(); }
  this.enableSync = function() { sync(1); }
  this.disableSync = function() { sync(0); }
  this.loadImg = function(_ID, _img) { if(typeof(imgMan[_ID].loadImg) == 'function') imgMan[_ID].loadImg(_img); }
  this.resetImg = function(_ID) { if(typeof(imgMan[_ID].resetImg) == 'function') imgMan[_ID].resetImg(); }
  this.enableDrop = function(_ID) { if(typeof(imgMan[_ID].enableDrop) == 'function') imgMan[_ID].enableDrop(); }
  this.disableDrop = function(_ID) { if(typeof(imgMan[_ID].disableDrop) == 'function') {imgMan[_ID].disableDrop();} }
  this.enableLock = function(_ID) { if(typeof(imgMan[_ID].enableLock) == 'function') imgMan[_ID].enableLock(); }
  this.disableLock = function(_ID) { if(typeof(imgMan[_ID].disableLock) == 'function') imgMan[_ID].disableLock(); }
  this.enableSyncManual = function() { manualSync = 1; }
  this.disableSyncManual = function() { manualSync = 0; }
  this.setDebug = function(_val) { imgMan[0].debug = _val; imgMan[1].debug = _val; imgCmp.debug = _val; self.debug = _val;}

  this.setAnnotateCnvReq = function(_ar) { imgMan[0].setAnnotateCnvReq(_ar); imgMan[1].setAnnotateCnvReq(_ar); };
  this.setVerifyReq = function(_obj) { verifyReq = _obj; };

  this.createInfoTable = function(headers, values) { imgInfo.createOrChangeInfoTable(headers, values); };
  this.getNotesFromTxtArea = function() { return imgInfo.getTxtBoxValue() };
  this.getPositionCmp = function() {return cmp};
  this.getModifiedImage = function(_ID) { return imgMan[_ID].getCanvasData(); };
  this.getOriginalImage = function(_ID) { return imgMan[_ID].getImgData(); };
  this.getImageFromCmp = function(_ID) { return imgCmp.getOptimizedImage(); }

  function verifyOE() {
    if(typeof(verifyReq)== 'function' ) {
      var obj0 = imgMan[0].getImgData();
      var obj1 = imgMan[1].getImgData();
      verifyReq(obj0, obj1, 0);
    }
  };
  function verifyE() {
    if(typeof(verifyReq)== 'function' ) {
      var obj0 = imgMan[0].getCanvasData();
      var obj1 = imgMan[1].getCanvasData();
      verifyReq(obj0, obj1, 1);
    }
  };
  this.setScore = function (_score) {
    //console.log('Verify res SETted!!!'+ _score);
    score.innerHTML = _score.toString()+ '%';
  };
  this.dropCB = function () {
    //console.log('DROP CB!!!');
    score.innerHTML = '';
  };

  this.enableEyeLines = function() { lines = 1; imgMan[0].enableEyeLines(); imgMan[1].enableEyeLines(); imgCmp.enableEyeLines();}
  this.disableEyeLines = function() { lines = 0; imgMan[0].disableEyeLines(); imgMan[1].disableEyeLines(); imgCmp.disableEyeLines();}
  /*
  this.enableVerify = function () {
    elem.appendChild(verify);
    elem.appendChild(verifyO);
    verify.addEventListener('click', verifyE, false);
    verifyO.addEventListener('click', verifyOE, false); 
  }
  */
  this.disableVerify = function () { 
    elem.removeChild(verify);
    elem.removeChild(verifyO); 
    verify.removeEventListener('click', verifyE, false);
    verifyO.removeEventListener('click', verifyOE, false);
  }
  function toggleSync() { if (manualSync) { if(synced) sync(0); else sync(1);} }
  function sync(val) {
    if (val== 0) { synced = 0; lckImg.src = iUnlock; imgMan[0].sync = 0; imgMan[1].sync = 0; }
    else         { synced = 1; lckImg.src = iLock; imgMan[0].sync = 1; imgMan[1].sync = 1; }
  }
  function toggleGS() {
    if (gs) { gs = 0; gsImg.src = iBWC; imgMan[0].disableGS(); imgMan[1].disableGS(); }
    else    { gs = 1; gsImg.src = iBW;  imgMan[0].enableGS(); imgMan[1].enableGS(); }
  }
  function toggleEL() {
    if (lines)   { self.disableEyeLines(); } 
    else         { self.enableEyeLines(); }
  }
  function detectBrowserWindowChange() {
	  var currentZoomRatio = Math.round(window.devicePixelRatio * 100);	  	  
	  if (currentZoomRatio == prevZoomRatio) {			  
		  bottom = window.innerHeight;			  	  
	  } else {
		  prevZoomRatio = currentZoomRatio;
	  }	  
  }  
  function reCalc(onResize, isSucher) {		
    //log('reCalc');	
	if (onResize) {
		// Move the everything with the resining of the page with other elements.
		var bodyRect = document.body.getBoundingClientRect();		
		var element = document.getElementById("imgcmp");
		var rect = element.getBoundingClientRect();		
		left = Math.abs(bodyRect.left) + rect.left;		
	}	
    var space1 = 80; var space2 = 40;    

	var maxHeightOfElement = bottom - top - bottomSpacing; // bottom border spacing.
	if (maxHeightOfElement < 450) {
		// Minimal allowed height of drawed components - it was calculated as minimal height
		// that fits to 1920x1080 with system 125% zoom.
		maxHeightOfElement = 450;
	}	

    l.x = Math.round(left); 
	l.y = Math.round(top);	
	l.h = 2 * Math.round(maxHeightOfElement / 1.4 / 2); // 1.4 is ratio that how much smaller will be left canvas.
	l.w = 2 * Math.round(l.h / 1.3 / 2); // 1.3 is ratio 4:3 between width and height.

    r.x = l.x + l.w + space1;
    r.y = l.y;
    r.w = l.w; 
	r.h = l.h;

	cmp.x = l.x + l.w + space1 + r.w + space2;
	cmp.y = top;    	
	cmp.h = 2 * Math.round(maxHeightOfElement / 2);	
	cmp.w = 2 * Math.round(cmp.h / 1.3 / 2);	

    log('LR   W: '+ l.w+ ' H: '+ l.h);
    log('L    X: '+ l.x+ ' Y: '+ l.y);
    log('R    X: '+ r.x+ ' Y: '+ r.y);
    log('CMP  W: '+ cmp.w+ ' H: '+ cmp.h);

    imgInfo.init(l, r);
    imgCmp.init(cmp); imgMan[0].init(l); imgMan[1].init(r); 

    lckImg.style.width = (40).toString() + 'px'; lckImg.style.height = (40).toString() + 'px';
    gsImg.style.width = (40).toString() + 'px'; gsImg.style.height = (40).toString() + 'px';
    lnImg.style.width = (40).toString() + 'px'; lnImg.style.height = (40).toString() + 'px';
    verify.style.width = (40).toString() + 'px'; verify.style.height = (40).toString() + 'px';
    verifyO.style.width = (40).toString() + 'px'; verifyO.style.height = (40).toString() + 'px';

    score.style.fontSize = '20px';
    score.style.fontFamily = 'Arial';
    score.style.textAlign = "center";
    var xpos = l.x + l.w + ((space1 / 2) - 20);
    score.style.left = (xpos - 18).toString() + 'px';
    lckImg.style.left = xpos.toString() + 'px';
    gsImg.style.left = xpos.toString() + 'px';
    lnImg.style.left = xpos.toString() + 'px';
    verify.style.left = xpos.toString() + 'px';
    verifyO.style.left = xpos.toString() + 'px';

    score.style.top = (top + 50).toString() + 'px';
    lckImg.style.top = (top + 100).toString() + 'px';
    gsImg.style.top = (top + 150).toString() + 'px';
    lnImg.style.top = (top + 200).toString() + 'px';
    verify.style.top = (top + 250).toString() + 'px';
    verifyO.style.top = (top + 300).toString() + 'px';
  }

}
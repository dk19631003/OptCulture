zk.afterLoad('zk', function () {
        zk.override(zk.Widget.prototype, {}, {
                getDragOptions_: function (map) {
                        map = zk.copy({}, map);
                        var ghosting = map.ghosting,
                                endeffect = map.endeffect;
                        
                        map.ghosting = function(drag, ofs, evt) {
                                var node = drag.handle,
                                        cloneNode = ghosting(drag, ofs, evt);
                                if (!node.parentNode.className.match('z-listcell'))
                                        node.style.display = 'none';
                                return cloneNode;
                        };
                        
                        map.constraint = function(drag, pt, evt) {
                                var ofs = drag.handle.parentNode.className.match('z-listcell') ? 
                                                                [-5,-5]:drag.offset;
                                return [evt.pageX - ofs[0], evt.pageY - ofs[1]];
                        };
                        
                        map.endeffect = function(drag, evt) {
                                var centerDiv = zk.Widget.$('$centerDiv'),
                                        centerDivOfs = zk(centerDiv).revisedOffset(),
                                        winOfs = zk(zk.Widget.$('$programdesignerWinId')).revisedOffset(),
                                        ofs = drag.offset,
                                        $node = jq(centerDiv.$n()),
                                        top = $node.offset().top,
                                        bottom = top + $node.height(),
                                        left = $node.offset().left,
                                        right = left + $node.width();
        
                                evt.data.pageX = evt.pageX - (centerDivOfs[0] - winOfs[0]) - ofs[0];
                                evt.data.pageY = evt.pageY - (centerDivOfs[1] - winOfs[1]) - ofs[1];
                                
                                if (evt.pageX > left && evt.pageX < right &&
                                                evt.pageY > top && evt.pageY < bottom) {
                                        endeffect(drag, evt);
                                        window.showPrevNode = function(){
                                                drag.handle.style.display = '';
                                                delete window.showPrevNode;
                                        };
                                } else {
                                        drag.handle.style.display = '';
                                }
                        };
                        return map;
                }
        });
});
zk.afterLoad('zul.wnd', function () {
        zul.wnd.Window._aftersizing = function(dg, evt) {
                var wgt = dg.control,
                        data = dg.z_szofs;
                
                if (wgt._sclass == 'myWin') {
                        var centerDivOfs = zk(zk.Widget.$('$centerDiv')).revisedOffset();
                        data.left = jq.px(zk.parseInt(data.left) - centerDivOfs[0]);
                        data.top = jq.px(zk.parseInt(data.top) - centerDivOfs[1]);
                }
                
                wgt.fire('onSize', zk.copy(data, evt.keys), {ignorable: true});
                dg.z_szofs = null;
        };
});

var nomnoml = nomnoml || {}

$(document).ready(function (){

    var jqCanvas = $('#canvas')
    var viewport = $(window)
    var jqBody = $('body')
    var imgLink = document.getElementById('savebutton')
    var canvasElement = document.getElementById('canvas')
    var canvasPanner = document.getElementById('canvas-panner')
    var canvasTools = document.getElementById('canvas-tools')
    var defaultSource = document.getElementById('defaultGraph').innerHTML
    var zoomLevel = 0
    var offset = {x:0, y:0}
    var mouseDownPoint = false
    var vm = skanaar.vector

    canvasPanner.addEventListener('mouseenter', classToggler(jqBody, 'canvas-mode', true))
    canvasPanner.addEventListener('mouseleave', classToggler(jqBody, 'canvas-mode', false))
    canvasTools.addEventListener('mouseenter', classToggler(jqBody, 'canvas-mode', true))
    canvasTools.addEventListener('mouseleave', classToggler(jqBody, 'canvas-mode', false))
    canvasPanner.addEventListener('mousedown', mouseDown)
    window.addEventListener('mousemove', _.throttle(mouseMove,50))
    canvasPanner.addEventListener('mouseup', mouseUp)
    canvasPanner.addEventListener('mouseleave', mouseUp)
    canvasPanner.addEventListener('wheel', _.throttle(magnify, 50))
    initImageDownloadLink(imgLink, canvasElement)

    function classToggler(element, className, state){
        var jqElement = $(element)
        return _.bind(jqElement.toggleClass, jqElement, className, state)
    }

    function mouseDown(e){
        $(canvasPanner).css({width: '100%'})
        mouseDownPoint = vm.diff({ x: e.pageX, y: e.pageY }, offset)
    }

    function mouseMove(e){
        if (mouseDownPoint){
            offset = vm.diff({ x: e.pageX, y: e.pageY }, mouseDownPoint)
            sourceChanged()
        }
    }

    function mouseUp(){
        mouseDownPoint = false
        $(canvasPanner).css({width: '100%'})
    }

    function magnify(e) {
        zoomLevel = Math.min(10, zoomLevel - (e.deltaY < 0 ? -1 : 1))
        sourceChanged()
    }

    nomnoml.magnifyViewport = function (diff) {
        var baseZoomLevel = zoomLevel
        var loadZoom = function () {
            return baseZoomLevel + diff * 0.25;
        }
        zoomLevel = loadZoom()
        sourceChanged()
        zoomLevel = loadZoom()
        sourceChanged()
    }

    nomnoml.resetViewport = function () {
        zoomLevel = -8
        var loadOffset = function() {
            return {
                x: (canvasElement.width / 4) * -0.25,
                y: 0
            }
        }
        offset = loadOffset()
        sourceChanged()
        zoomLevel = -8
        offset = loadOffset()
        sourceChanged()
    }

    nomnoml.toggleSidebar = function (id){
        var sidebars = ['reference', 'about']
        _.each(sidebars, function (key){
            if (id !== key) $(document.getElementById(key)).toggleClass('visible', false)
        })
        $(document.getElementById(id)).toggleClass('visible')
    }

    nomnoml.exitViewMode = function (){
        window.location = './'
    }

    function urlEncode(unencoded) {
        return encodeURIComponent(unencoded).replace(/'/g,'%27').replace(/"/g,'%22')
    }

    function urlDecode(encoded) {
        return decodeURIComponent(encoded.replace(/\+/g, ' '))
    }

    function buildStorage(locationHash){
        var key = 'nomnoml.lastSource'
        if (locationHash.substring(0,6) === '#view/')
            return {
                read: function (){ return urlDecode(locationHash.substring(6)) },
                save: function (){ },
                moveToLocalStorage: function (){ localStorage[key] = currentText() },
                isReadonly: true
            }
        return {
            read: function (){ return localStorage[key] || defaultSource },
            save: function (source){
                localStorage[key] = source
            },
            moveToLocalStorage: function (){},
            isReadonly: false
        }
    }

    function initImageDownloadLink(link, canvasElement){
        link.addEventListener('click', downloadImage, false);
        function downloadImage(){
            var url = canvasElement.toDataURL('image/png')
            link.href = url;
        }
    }

    function positionCanvas(rect, superSampling, offset){
        var w = rect.width / superSampling
        var h = rect.height / superSampling
        // top: h * (1 - h/viewport.height()) / 2 + offset.y,
        // left: (w / 4) + (viewport.width() - w)/2 + offset.x,
        jqCanvas.css({
            top: h * (1 - h/viewport.height()) / 4 + offset.y,
            left: (w / 4) + (viewport.width() - w)/2 + offset.x,
            width: w,
            height: h
        })
    }

    function setFilename(filename){
        imgLink.download = filename + '.png'
    }

    function currentText(){
        return defaultSource
    }

    function sourceChanged(){
        try {
            var superSampling = window.devicePixelRatio || 1
            var scale = superSampling * Math.exp(zoomLevel/10)

            nomnoml.draw(canvasElement, currentText(), scale)
            positionCanvas(canvasElement, superSampling, offset)
            setFilename('netuno-diagram')
        } catch (e) {
            throw e
        }
    }
    window.setTimeout(function () {
        nomnoml.resetViewport()
    }, 1000);
    //sourceChanged()
})

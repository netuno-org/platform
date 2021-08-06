
_logger.info('\n'+
    '#\n'+
    '# Firebase listener '+ _req['key'] +': '+ _req['path'] +'\n'+
    '#\n'+
    _req.getValues('value').toJSON()
)

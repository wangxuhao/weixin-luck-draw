define(function () {
    /** Read xml file and return E4X object, support UTF-8 encoding only */
    var loadE4X = function(xmlFile){
        var f = xmlFile + "";
        var result = null;
        require(["std/os"], function(os){
        	var xml = os.readTextFile(f);
        	if (!xml){
        		throw "Can't read file: " + f;
        	}
        	//Trim the header "<?xml version="1.0" encoding="UTF-8"?>"
        	if (0==xml.indexOf("<?xml")){
        		secondLt = xml.indexOf("<", 5);
        		xml = xml.substring(secondLt);
        	}
        	result = new XML(xml);
        });
        return result;
    };
    
    /** Save an E4X object into xml file */
    var saveE4X = function(xmlFile, e4x){
    	var f = xmlFile + "";
        require(["std/os"], function(os){
        	var xml = e4x.toString();
        	//Add the header "<?xml version="1.0" encoding="UTF-8"?>"
        	xml = '<?xml version="1.0" encoding="UTF-8"?>' + "\n" + xml;
        	os.writeTextFile(xmlFile, xml);
        });
    }
    
    return {
    	loadE4X: loadE4X, saveE4X: saveE4X
    }
});
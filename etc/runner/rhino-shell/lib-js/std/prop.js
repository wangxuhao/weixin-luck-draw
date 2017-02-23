define(function () {
	var Properties = java.util.Properties;
	
    /** Read properties file and return java.util.Properties object */
    var loadProp = function(propFile){
        var file = new java.io.File(propFile + "");
        var result = new Properties();
        if (file.exists()){
        	result.load(new java.io.FileReader(file));
        }
        return result;
    };
    
    /** Save to properties file */
    var saveProp = function(propFile, properties, comments){
    	var file = new java.io.File(propFile + "");
    	file.getParentFile().mkdirs();
    	properties.store(new java.io.FileWriter(file), comments);
    };
    
    /** Append properties to properties file */
    var appendProp = function(propFile, properties, comments){
    	var file = new java.io.File(propFile + "");
    	file.getParentFile().mkdirs();
    	properties.store(new java.io.FileWriter(file, true), comments);
    }
    
    return {
    	loadProp: loadProp, saveProp: saveProp, appendProp: appendProp
    }
});
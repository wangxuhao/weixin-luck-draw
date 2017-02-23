/** Utility methods based on json2.js */
/* ================================= */
define(function () {
    var util = {};
    require (["std/utils/json2"], function(JSON) {
/* ================================= */

var JSON__normalize = function(json){
    var obj = eval('(' + json + ')');
    return JSON.stringify(obj);
}
var JSON__merge = function(target, patch, config){
    var to = eval('(' + target + ')');
    var po = eval('(' + patch + ')');
    JSON__merge_obj(null, to, po, config);
    return JSON.stringify(to);
}
var JSON__merge_obj = function(dataItemName, target, patch, config){
    if (target instanceof Array){
        JSON__merge_array(dataItemName, target, patch, config);
        return;
    }
	for (var item in patch){
		var value = patch[item];
		var fullName = item;
		if (dataItemName){
			fullName = dataItemName + "/" + item;
			logger.debug("Merge FullName: " + fullName);
		}
		if (value){
			if ((config)&&(config.isSingleItem(fullName))){
				target[item] = value;
			}else if (typeof(value)=="object"){
				if (target[item]){
					JSON__merge_obj(fullName, target[item], value, config);
				}else{
					target[item] = value;
				}
			}else{
				target[item] = value;
			}
		}else if (typeof(value)==typeof(false)){	//value is "false"
			target[item] = value;
		}else if (typeof(value)==typeof(0)){		//value is "0"
			target[item] = value;
		}
	}
}
var JSON__merge_array = function(dataItemName, targetArray, patchArray, config){
    if (patchArray){
        if (patchArray instanceof Array){	//Patch is array
            var pLen = patchArray.length;
            for(var i=0; i<pLen; i++){
                JSON__merge_array_append(dataItemName, targetArray, patchArray[i], config);
            }
         }else{					//Patch is an object
            JSON__merge_array_append(dataItemName, targetArray, patchArray, config);
        }
    }
}
var JSON__merge_array_append = function(dataItemName, targetArray, patchItem, config){
	var idKey = config.getArrayItemKey(dataItemName);
	if (idKey && patchItem[idKey]){
		var idValue = patchItem[idKey];
		var len = targetArray.length;
		for(var i=0; i<len; i++){
			var id = targetArray[i][idKey];
			if (id && id==idValue){
				JSON__merge_obj(dataItemName + "[]", targetArray[i], patchItem, config);
				return;
			}
		}
	}
	//Not found ...
	targetArray[targetArray.length] = patchItem;
}

//From http://www.jslab.dk/tools.jsonformat.php
function jslab_JSONFormat(json, level) {
	var indent = '\t';
	  
	var tab = '';
	if (!level) level = 0;
	for(var i=0; i<level; i++){
		tab += indent;
	}
	  
    var oCon = json.constructor.name;
    var p1 = null;
    var v = null;
    var pCon = null;
    var s = (oCon == 'Object' ? '{' : '[');
    for(var p in json) {
		pCon = (null!=json[p]&&undefined!=json[p])?json[p].constructor.name:"__NULL";

        if (oCon == 'Array' && !/^\d+$/.test(p))
            continue;
        p1 = /\s/.test(p) ? '\'' + p + '\'' : p;
        if (oCon == 'Array')
            s += '\n' + tab + indent;
        else
            s += '\n' + tab + indent + p1 + ': ';
        if (pCon == 'Object' || pCon == 'Array'){
            //s += '\n' + jslab_JSONFormat(json[p], l + 2) + ',';
		    var tmp = jslab_JSONFormat(json[p], level + 1);
		    tmp = tmp.replace(/(^\s*)/g, "");	//LTrim
		    s += tmp + ',';
        } else if (pCon == 'Function') {
            //s += '\n' + tab + '    ' + json[p].toString().replace(/\n/g, '\n    ' + tab) + ',';
		    var f = json[p].toString();
		    var tmp = f.replace(/\n/g, '\n' + tab + indent);
		    s += tmp + ',';
        } else {
            v = pCon == 'String' ? '\'' + json[p] + '\'' : json[p];
            s += v + ',';
        }
    }
    s = s.substring(0, s.length - 1);
    s += '\n' + tab + (oCon == 'Object' ? '}' : ']');
    return s;
}

var JSON__formatter = function(json){
    if (null==json){
        return json;
    }

    var obj;
    if (typeof json == "string"){
        obj = eval('(' + json + ')');
    }else{
        obj = json;
    }
    return jslab_JSONFormat(obj);
}
/* ================================= */
        util = {
            normalize: JSON__normalize,
            merge: JSON__merge,
            format: JSON__formatter
        };
    });
    return util;
});
/* ================================= */

define(function () {
    var StringEscapeUtils = Packages.org.apache.commons.lang3.StringEscapeUtils;

    /** Replace ${XXX} in a string */
    var replacePlaceHolder = function(string, dataOrProvider, thisObj){
        if (!string || !dataOrProvider){
            return string;
        };
        if (! thisObj){
            thisObj = this;
        }
        string = string + "";   //Force to javascript string
        var result = string.replace(
            /\$\{(.*?)\}/g,
            function(s0){
                s0 = s0.substr(2,s0.length-3);
                var result = null;
                if (typeof dataOrProvider == "function"){
                    //If argument 2 is a provider
                    result = dataOrProvider.call(thisObj, s0);
                }else{
                    //Just the data
                    result = dataOrProvider[s0];
                }
                if (null==result){
                    result = "${" + s0 + "}";
                }
                return result;
            }
        );
        return result;
    };
    
    /** Trim string or string array */
    var trim = function(str){
        if (! str){
            return str;
        }
        if (str.join){
            //If argument is an array, trim every element
            for(var i=0; i<str.length; i++){
                str[i] = trim(str[i]);
            }
            return str;
        }else{
            var jstr = new java.lang.String(str);
            jstr = jstr.trim();
            return jstr + "";
        }
    };
    
    /** startsWith */
    var startsWith = function(str, found) {
        if (null==str){
            return false;
        }
        if (null==found){
            return true;
        }
        var test = str.substr(0, found.length);
        return (test==found);
        /* TESTCASE
         //
         startsWith(null, null)          //false
         startsWith(null, "123")         //false
         startsWith("123*.jar", null)    //true
         startsWith("123*.jar", "123")   //true
         startsWith("123*.jar", "1234")  //false
         startsWith("123", "123")        //true
         startsWith("123", "1234")       //false
         */
    }
    
    /** endsWith */
    var endsWith = function(str, found) {
        if (null==str){
            return false;
        }
        if (null==found){
            return true;
        }
        var test = str.substr(str.length - found.length);
        return (test==found);
        /* TESTCASE
        //
        endsWith(null, null)           //false
        endsWith(null, "123")          //false
        endsWith("123*.jar", null)     //true
        endsWith("123*.jar", "*.jar")  //true
        endsWith("123*.jar", "*_jar")  //false
        endsWith("123", "123")         //true
        endsWith("123", "1234")        //false
        */
    };
    
    /** Get every element of an array and process */
    var arrayForEach = function(array, fn, thisObj){
        if (!array || !array.join || !fn){
            return;
        }
        if (! thisObj){
            thisObj = this;
        }
        for(var i=0; i<array.length; i++){
            fn.call(thisObj, array[i], i);
        }
    }
    
    /** Escape and native2ascii for properties file key or value */
    var prepare4Property = function(str){
        if (str){
            var s = new java.lang.String(str+"");
            s = StringEscapeUtils.escapeJava(s);
            return s+"";
        }else{
            return str;
        }
    }
    
    /** Escape and native2ascii for properties file, with all attributes in object
     * obj: object which attribute name and value should be prepared for properties file
     * bothValueAndKey: prepare for both attribute name and value; default is value only
     */
    var prepare4Properties = function(obj, bothValueAndKey){
        var tmp = {};
        for (var key in obj){
            var val = obj[key];
            if (bothValueAndKey){
                tmp[prepare4Property(key)] = prepare4Property(val);
            }else{
                tmp[key] = prepare4Property(val);
            }
        }
        return tmp;
    }
    
    /** Join several parts to a path string */
    var joinPath = function(path){
        if (path.join){
            for (var i=0; i<path.length; i++){
                path[i] = path[i]+"";   //Force to javascript string
                if (i>0){   //Remain heading "/" for first path element 
                    path[i] = path[i].replace(/^\//, "");   //The start "/"
                    path[i] = path[i].replace(/^\\/, "");   //The start "\"
                }
                path[i] = path[i].replace(/\/$/, "");   //The end "/"
                path[i] = path[i].replace(/\\$/, "");   //The end "\"
            }
            path = path.join(Packages.java.io.File.separator);
            return path;
        }else{
            return path;
        }
    }
    
    return {
        replacePlaceHolder: replacePlaceHolder,
        trim: trim,
        startsWith: startsWith,
        endsWith: endsWith,
        arrayForEach: arrayForEach,
        prepare4Property: prepare4Property,
        prepare4Properties: prepare4Properties,
        joinPath: joinPath
    }
});
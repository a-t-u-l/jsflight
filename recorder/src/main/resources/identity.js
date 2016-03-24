/**
 * Method to identify elements 
 */
// jsflight namespace 
var jsflight = jsflight || {};

/**
 * Based on firebug method of getting xpath of dom element
 */
jsflight.getElementXPath = function(element) {
    if (element && element.id)
        return '// *[@id="' + element.id + '"]';
    else
        return jsflight.getElementTreeXPath(element);
};

jsflight.checkIdIsNotExcluded = function(id){
  return !jsflight.options.exclusion_regexp.test(id);
};

jsflight.getElementXpathId = function(element){
    var paths = [];
    for (; element && element.nodeType == 1; element = element.parentNode) {
        var attr = element.attributes;
        var idr = element.id;
        var tag = element.tagName.toLowerCase();
        if(!idr && tag == "input"){
            paths.push("//"+tag);    
        }
        if(idr && jsflight.checkIdIsNotExcluded(idr)){
            paths.splice(0,0,"//*[@id='"+idr+"']");
        } else {
            var to_store = jsflight.options.attributes_to_store;
            for(i=0;i<to_store.length;i++){
                if(attr.hasOwnProperty(to_store[i])){
                    paths.splice(0,0,"//*[@"+to_store[i]+"='"+ attr.getNamedItem(to_store[i]).nodeValue + "']");
                }
            }
        }
    }
    return paths.join("");
};

jsflight.getElementTreeXPath = function(element) {
    var paths = [];

    // Use nodeName (instead of localName) so namespace prefix is included (if
    // any).
    for (; element && element.nodeType == 1; element = element.parentNode) {
        var index = 0;
        for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {
            // Ignore document type declaration.
            if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE)
                continue;

            if (sibling.nodeName == element.nodeName)
                ++index;
        }

        var tagName = element.nodeName.toLowerCase();
        var pathIndex = (index ? "[" + (index + 1) + "]" : "");
        paths.splice(0, 0, tagName + pathIndex);
    }

    return paths.length ? "/" + paths.join("/") : null;
};

jsflight.getTargetId = function(event) {
    var paths = [];
    var target = event.target;
    
    if(target===null || target === undefined)
        return paths;
    
    var elt = target;
    do{
        paths.push(jsflight.getElementFullId(elt));
        elt = elt.parentNode;
    }while(elt!==undefined && elt!==null && elt!=document);
    
    return paths;
};

jsflight.getElementFullId = function(target) {
    if (target === undefined) {
        return null;
    }

    if (target === null) {
        return null;
    }
    
    if(target==window) {
        return null;
    }
    
    if(target==document) {
        return null;
    }

    return {
        getxp: Xpath.getElementTreeXPath(target),
        gecp: Css.getElementCSSPath(target),
        gecs: Css.getElementCSSSelector(target),
        csg: new CssSelectorGenerator().getAllSelectors(target)
        /* ,
         csg1 : new CssSelectorGenerator(['id', 'class', 'tag', 'nthchild']).getSelector(target),
         csg2 : new CssSelectorGenerator(['class', 'tag', 'nthchild']).getSelector(target),
         csg3 : new CssSelectorGenerator(['tag', 'nthchild']).getSelector(target),
         csg4 : new CssSelectorGenerator(['nthchild']).getSelector(target)
         */
    };
};


/**
 * Generate browser window/tab uuid
 * 
 * @returns {String}
 */
jsflight.guid = function() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16)
                .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
};

package org.nuxeo.template.brochure;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.template.api.context.DocumentWrapper;

public class BrochureContext {

    protected final DocumentModel doc;

    protected final DocumentWrapper nuxeoWrapper;
    protected Map<String, Object> ctx;

    public BrochureContext(DocumentModel doc, DocumentWrapper nuxeoWrapper,Map<String, Object> ctx) {
        this.doc = doc;
        this.nuxeoWrapper = nuxeoWrapper;
        this.ctx=ctx;
    }

    /**
     * Returns the list of the Parts children of the current Brochure doc if the current user is member of part:group metadata, or if the metadata is not set.
     *
     * @return List<Object>
     * @throws Exception
     */

    public List<Object> getChildrenPartDocForCurrentUser() throws Exception {
        List<DocumentModel> children = doc.getCoreSession().getChildren(
                doc.getRef());

        NuxeoPrincipal username = (NuxeoPrincipal) doc.getCoreSession().getPrincipal();
        List<Object> docs = new ArrayList<Object>();
        for (DocumentModel child : children) {
            if(child.getProperty("part", "group")!=null) {
                if(username.isMemberOf(child.getProperty("part", "group").toString())){
                    docs.add(nuxeoWrapper.wrap(child));
                }
            }
            else {
                docs.add(nuxeoWrapper.wrap(child));
            }


        }
        return docs;
    }



}

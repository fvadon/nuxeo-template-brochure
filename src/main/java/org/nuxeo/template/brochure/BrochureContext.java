package org.nuxeo.template.brochure;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
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
     * Returns the list of the Parts of the current Brochure doc for which the Criteria group matches on the current User group
     *
     * @return List<Object>
     * @throws Exception
     */

    public List<Object> getChildrenPartDocForCurrentUser() throws Exception {
        List<DocumentModel> children = doc.getCoreSession().getChildren(
                doc.getRef());
        List<Object> docs = new ArrayList<Object>();
        for (DocumentModel child : children) {
            docs.add(nuxeoWrapper.wrap(child));
        }
        return docs;
    }



}

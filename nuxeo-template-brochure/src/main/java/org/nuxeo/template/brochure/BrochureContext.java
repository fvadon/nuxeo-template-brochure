package org.nuxeo.template.brochure;


import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
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
     * Returns the document from the document ID
     * @param documentId
     * @return
     * @throws Exception
     */

    public Object getDocument(String documentId) throws Exception {
        DocumentRef ref = new IdRef(documentId);
        return nuxeoWrapper.wrap(doc.getCoreSession().getDocument(ref));
    }

}

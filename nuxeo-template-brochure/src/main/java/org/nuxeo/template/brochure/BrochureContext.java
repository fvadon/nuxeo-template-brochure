package org.nuxeo.template.brochure;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.template.api.context.DocumentWrapper;
import org.nuxeo.template.context.BlobHolderWrapper;

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
     * Returns the list of Parts of the current Brochure doc if the current user is member of part:group metadata, or if the metadata is not set.
     * Brochure is folderish, Parts are children of Brochure
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

    /**
     * Returns a blob holder from the document ID
     * @param documentId
     * @return
     */
    public Object getBlobsForDocument(String documentId) {
        DocumentRef ref = new IdRef(documentId);
        return new BlobHolderWrapper(doc.getCoreSession().getDocument(ref));
    }

}

package cuina.editor.script.ruby;

public interface TreeEditorListener
{
    public void treeNodeAdded(TreeEditorEvent ev);
    public void treeNodeRemoved(TreeEditorEvent ev);
    public void treeNodeChanged(TreeEditorEvent ev);
//    public void selectionChanged(TreeEditorEvent ev);
}
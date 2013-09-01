package cuina.editor.core.internal;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CuinaPerspective implements IPerspectiveFactory
{
	public static final String ID 			= "cuina.editor.core.perspective";
	public static final String ID_TILESET 	= "cuina.editos.map.TilsetView";
	public static final String ID_AUTOTILES = "cuina.editos.map.AutotileView";

	@Override
	public void createInitialLayout(IPageLayout layout)
	{
		layout.addPerspectiveShortcut(ID);
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		
		defineActions(layout);
		defineLayout(layout);
	}

	private void defineLayout(IPageLayout layout)
	{
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		layout.addNewWizardShortcut("cuina.editor.ui.new.project");
		layout.addNewWizardShortcut("cuina.editor.map.new.map");

		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(ID_TILESET);
		layout.addShowViewShortcut(ID_AUTOTILES);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}

	private void defineActions(IPageLayout layout)
	{
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.8f, editorArea);
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
		

		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		right.addView(ID_TILESET);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_TASK_LIST);
	}
}

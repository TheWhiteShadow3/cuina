package test;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class SubmenuSnippet
{
	static int index = 0;

	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText(SubmenuSnippet.class.getSimpleName());
		shell.setLayout(new FillLayout());

		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));

		final Action showList = new DropDownAction(shell);

		final Link menuOwner = new Link(parent, SWT.NONE);
		menuOwner.setText("<a>Click To Show Menu</a>");
		menuOwner.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// bug appears only for the first time manager creates its menu,
				// so here we create new manager each time to repeat easier
				MenuManager menuManager = new MenuManager();
				menuManager.add(showList);
				menuManager.createContextMenu(menuOwner).setVisible(true);
			}
		});

		// there are no problems with such contribution to the ToolBarManager
		ToolBarManager toolBarManager = new ToolBarManager(SWT.NONE);
		toolBarManager.add(showList);
		toolBarManager.createControl(parent);

		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		display.dispose();
	}

	public static class DropDownAction extends Action implements IMenuCreator
	{
		private Menu myDynamicMenu;
		private Shell myShell;

		public DropDownAction(Shell shell)
		{
			myShell = shell;
			setText("Submenu Bar");
			setMenuCreator(this);
		}

		@Override
		public Menu getMenu(Menu parent)
		{
			return doGetMenu(parent);
		}

		@Override
		public Menu getMenu(Control parent)
		{
			return doGetMenu(parent);
		}

		private Menu doGetMenu(Object parent)
		{
			if (myDynamicMenu != null)
			{
				myDynamicMenu.dispose();
			}
			if (parent instanceof Menu)
			{
				myDynamicMenu = new Menu((Menu) parent);
			}
			else
			{
				myDynamicMenu = new Menu((Control) parent);
			}
			addEntries(myDynamicMenu);
			return myDynamicMenu;
		}

		@Override
		public void dispose()
		{
			if (myDynamicMenu != null)
			{
				myDynamicMenu.dispose();
				myDynamicMenu = null;
			}
		}

		protected void addActionToMenu(Menu parent, Action action)
		{
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(parent, -1);
		}

		private void addEntries(final Menu menu)
		{
			for (int i = 0; i < 5; i++)
			{
				final String next = "Menu Item " + i;
				final int myIndex = i;
				Action action = new Action(next, IAction.AS_RADIO_BUTTON)
				{
					@Override
					public void run()
					{
						if (isChecked())
						{
							index = myIndex;
//							MessageDialog.openInformation(menu.getShell(), "Activated Menu Item", next);
						}
					}
				};
				action.setChecked(i == index);
				addActionToMenu(menu, action);
			}
		}

		@Override
		public void run()
		{
			MessageDialog.openInformation(myShell, "Default Action", "Invoked Defaul Action");
		}
	}
}

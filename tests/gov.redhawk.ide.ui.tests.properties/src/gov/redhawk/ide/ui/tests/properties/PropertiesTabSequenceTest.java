/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.properties;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.prf.internal.ui.editor.detailspart.SimpleSequencePropertyDetailsPage;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class PropertiesTabSequenceTest extends AbstractBasicTest {

	@Test
	public void testValues() throws CoreException {
		TableViewer valuesViewer = getValuesViewer();
		// Start with type selected as string
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("true");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "a");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "true");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("true");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("char");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		bot.button("OK").click();
		writeToCell(valuesViewer, 0, 0, "abc");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		writeToCell(valuesViewer, 0, 0, "al");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("float (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "-1.1+jjak");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "-1.1+j10.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("long (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("longlong (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("short (16-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-11-j2");
		bot.button("OK").click();
		writeToCell(valuesViewer, 0, 0, "1");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "1+100iada");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "-11-j2");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11-j2");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ulong (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ulonglong (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ushort (16-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "1");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "1+j1ada");
		assertFormInvalid();
		writeToCell(valuesViewer, 0, 0, "11");
		assertFormValid();
		writeToCell(valuesViewer, 0, 0, "11+j2");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("objref");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.button("Cancel").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("string");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("abcd");
		bot.button("OK").click();
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("efg");
		bot.button("OK").click();
		assertFormValid();
		clearValues();
	}

	private TableViewer getValuesViewer() {
		final TableViewer[] retVal = new TableViewer[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);
				PropertiesFormPage propPage = spdEditor.getPropertiesPage();
				SimpleSequencePropertyDetailsPage sequencePage = (SimpleSequencePropertyDetailsPage) propPage.getPropertiesBlock().getDetailsPart().getCurrentPage();
				retVal[0] = sequencePage.getComposite().getValuesViewer();
			}

		});
		TableViewer valuesViewer = retVal[0];
		return valuesViewer;
	}

	private void clearValues() {
		SWTBotTable valuesTable = bot.tableWithLabel("Values:");
		if (valuesTable.rowCount() > 0) {
			for (int i = 0; i <= valuesTable.rowCount(); i++) {
				valuesTable.select(0);
				SWTBotButton removeButton = bot.button("Remove", 1);
				if (removeButton.isEnabled()) {
					bot.button("Remove", 1).click();
				} else {
					break;
				}
			}
		}
	}

	@Override
	protected void createType() {
		bot.button("Add Sequence").click();
	}

	@Override
	public void testEnum() throws CoreException {
		// Override to do nothing since Simple Sequences do not have enums
	}
	
	@Test
	public void testKind() {
		assertFormValid();
		
		SWTBotTable kindTable = bot.tableWithLabel("Kind:");
		Assert.assertFalse("Sequences don't support kind type execparam." , kindTable.containsItem("execparam"));
		
		kindTable.getTableItem("configure (default)").check();
		assertFormValid();
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("configure (default)").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").uncheck();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();

		
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("configure (default)").uncheck();
		kindTable.getTableItem("message").check();
		assertFormValid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
	}
}
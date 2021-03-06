/******************************************************************************
 * Copyright (C) 2008 SC ARHIPAC SERVICE SRL. All Rights Reserved.            *
 *****************************************************************************/
package org.idempiere.fa.process;

import org.compiere.model.MDepreciationExp;
import org.compiere.process.SvrProcess;


/**
 * @author Teo_Sarca, SC ARHIPAC SERVICE SRL
 */
@org.adempiere.base.annotation.Process
public class A_Depreciation_Exp_Process extends SvrProcess {
	
	protected void prepare()
	{
	}
	
	protected String doIt() throws Exception
	{
		MDepreciationExp depexp = new MDepreciationExp(getCtx(), getRecord_ID(), get_TrxName());
		depexp.process();
		return "@Processed@";
	}
}

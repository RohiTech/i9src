
package org.wms.process;

import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MLocator;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;

import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

public class GenerateLocatorsNEW extends SvrProcess
{
	private int M_Warehouse_ID = 0;
	
	private int M_LocatorType_ID = 0;
	
	private String prefix_X = "";
	
	private int X_from = 1;
	private int X_to = 1;
	
	private String prefix_Y = "";
	
	private int Y_from = 1;
	private int Y_to = 1;
	
	private String prefix_Z = "";
	
	private int Z_from = 1;
	private int Z_to = 1;
	
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		
		for (ProcessInfoParameter p:para)
		{
			String name = p.getParameterName();
				
			if (p.getParameter() == null)
				;
				else if(name.equals("M_Warehouse_ID")){
					M_Warehouse_ID = p.getParameterAsInt();
			}
				else if(name.equals("M_LocatorType_ID")){
					M_LocatorType_ID = p.getParameterAsInt();
			}
				else if(name.equals("prefix_X")){
					prefix_X = (String)p.getParameter();
			}
				else if(name.equals("X_from")){
					X_from = p.getParameterAsInt();
			}
				else if(name.equals("X_to")){
					X_to = p.getParameterAsInt();
			}
				else if(name.equals("prefix_Y")){
					prefix_Y = (String)p.getParameter();
			}
				else if(name.equals("Y_from")){
					Y_from = p.getParameterAsInt();
			}
				else if(name.equals("Y_to")){
					Y_to = p.getParameterAsInt();
			}
				else if(name.equals("prefix_Z")){
					prefix_Z = (String)p.getParameter();
			}
				else if(name.equals("Z_from")){
					Z_from = p.getParameterAsInt();
			}
				else if(name.equals("Z_to")){
					Z_to = p.getParameterAsInt();
			}
		}
	}
	
	private StringBuilder LocatorValueName = new StringBuilder();
	
	int x_f = 0;
	int y_f = 0;
	int z_f = 0;
	
	int x_t = 0;
	int y_t = 0;
	int z_t = 0;

	protected String doIt()
	{
		int cnt = 0;
		
		if (M_Warehouse_ID>0)
		{
			MWarehouse wh = new Query(Env.getCtx(),MWarehouse.Table_Name,MWarehouse.COLUMNNAME_M_Warehouse_ID+"=?",get_TrxName())
					.setParameters(M_Warehouse_ID)
					.setClient_ID()
					.first();
			
			cnt = createLocators(cnt, wh);
		}
		
		return "Locators Created "+cnt+", Last Locator Value: "+LocatorValueName.toString();
	}
	
	private int createLocators(int cnt, MWarehouse whse)
	{
		MLocator locator = null;
		
		for (int i=X_from;i<=X_to;i++)
		{
			for (int j=Y_from;j<=Y_to;j++)
			{
				for (int k=Z_from;k<=Z_to;k++)
				{
					String aisle = prefix_X + String.valueOf(i);
					
					String bin = prefix_Y + String.valueOf(j);
					
					String level = prefix_Z + String.valueOf(k);
					
					LocatorValueName = new StringBuilder(aisle+"-"+bin+"-"+level);
					
					locator = new MLocator(whse,LocatorValueName.toString());
					
					locator.set_ValueOfColumn("prefix_x", prefix_X);
					locator.set_ValueOfColumn("num_x", i);
					
					locator.set_ValueOfColumn("prefix_y", prefix_Y);
					locator.set_ValueOfColumn("num_y", j);
					
					locator.set_ValueOfColumn("prefix_z", prefix_Z);
					locator.set_ValueOfColumn("num_z", k);
					
					locator.setXYZ(aisle,bin,level);
					
					locator.setM_LocatorType_ID(M_LocatorType_ID);
					
					if(!existsLocator(locator.getValue()))
						locator.saveEx(get_TrxName());
					
					cnt++;
					
					log.info("Locator generated: "+locator.getValue());
					statusUpdate("Generate Locator " + locator.getValue());
				}
			}
		}
		
		// deleteLocators(whse);
		
		/*MLocator locator = null;
		
		char a = "A".charAt(0);
		char b = "A".charAt(0);
		char c= "A".charAt(0);

		int posx = x - 'A' + 1;
		int posy = y - 'A' + 1;
		int posz = z - 'A' + 1;
		
		if (Character.isDigit(x)){ 
			posx=new Integer(X);
		}
		if (Character.isDigit(y)){ 
			posy=new Integer(Y);
		}
		if (Character.isDigit(z)){ 
			posz=new Integer(Z); 
		} 
		
		if (locator!=null){
			whse.setM_ReserveLocator_ID(locator.get_ID());
			whse.saveEx(get_TrxName());
			locator.setIsDefault(true);
			locator.saveEx(get_TrxName());
		}*/
		
		return cnt;
	}
	
	public boolean existsLocator(String value)
	{
		boolean b = false;
		
		List<MLocator> locs = new Query(Env.getCtx(), MLocator.Table_Name,MLocator.COLUMNNAME_Value + "=?", get_TrxName())
				.setParameters(value)
				.setClient_ID().setOnlyActiveRecords(true).list();
		
		for (MLocator loc:locs)
		{
			//System.out.println(loc.getValue() + " EXISTS !!!");
			b = true;
		}
		
		return b;
	}
	

	public void deleteLocators(MWarehouse whse)
	{
		//delete old locators first?
				List<MLocator> oldlocs = new Query(Env.getCtx(),MLocator.Table_Name,MLocator.COLUMNNAME_M_Warehouse_ID+"=? AND M_Locator_ID>999999 ",get_TrxName())
						.setParameters(whse.get_ID())
						.setClient_ID()
						.list();
				
				for (MLocator loc:oldlocs)
				{
					MStorageOnHand soh = new Query(Env.getCtx(),MStorageOnHand.Table_Name,MStorageOnHand.COLUMNNAME_M_Locator_ID+"=?",get_TrxName())
							.setParameters(loc.get_ID())
							.first();
					
					if (soh==null)
					{
						log.info("Old locator deleted:"+loc.getValue());
						//loc.delete(false);
					}
				}
	}

}


package br.com.vale.fis.routes;

import br.com.vale.fis.components.ValeTibcoEMSComponent;
import br.com.vale.fis.log.EventCode;
import br.com.vale.fis.log.ValeLog;
import br.com.vale.fis.log.ValeLogger;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FromTibcoEMSToAzure extends RouteBuilder {
	

		@Value("${tibco.queueOut}")
		private String tibcoQueueOut;
	
		@Value("${tibco.host}")
		private String host;
	
		@Value("${tibco.user}")
		private String user;
	
		@Value("${tibco.password}")
		private String password;

  @Override
  public void configure() throws Exception {
  
	  onException(Exception.class)
	      .handled(true)
	      .bean(ValeLog.class, "logging(" + EventCode.E950 + ", ${exception.message})");
    
  from("tibco:".concat(tibcoQueueOut))
      .routeId("FromTibcoEMSToAzure")
      .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
      .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
      .setHeader("soapAction",constant("http://www.vale.com/EH/EH20160010_01/GetMasterData/PostMasterDataReponse" ))
      .to("https4://healthandsafetyapi-webservice-dev.azurewebsites.net/Services/MasterDataService.asmx")
      .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
  }
}

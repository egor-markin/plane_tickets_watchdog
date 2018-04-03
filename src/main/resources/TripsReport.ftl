<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${reportData.reportTitle}</title>
</head>
<body>

	<h1>Airplane prices: ${reportData.srcAirport.country}.${reportData.srcAirport.city} (${reportData.srcAirport.code}) <#if reportData.backDateStart??> <-> <#else> -> </#if> ${reportData.destAirport.country}.${reportData.destAirport.city} (${reportData.destAirport.code})</h1>

	<table border="1" cellpadding="1" cellspacing="1" >
	<thead>
		<tr>
			<th scope="row" width="100">
			<#if reportData.backDateStart??>
			BACK dates<br>
			</#if>
			TO dates
			</th>
			<#list reportData.backDates as bd>
			<th scope="col" width="100">${bd}</th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list reportData.toDates as td>
		<tr>
			<th scope="row">${td}</th>
			<#list reportData.backDates as bd>
				<#if reportData.getTrip(td, bd)??>
					<td align="center" style="background-color:#${reportData.getColor(td, bd)}"><a href="${reportData.getTrip(td, bd).momondoSearchUrl}" title="Stops: ${reportData.getTrip(td, bd).stops1} & ${reportData.getTrip(td, bd).stops2}; Durations: ${reportData.getTrip(td, bd).getTravelTime1Formatted()} & ${reportData.getTrip(td, bd).getTravelTime2Formatted()}; Stay days: ${reportData.getTrip(td, bd).getStayDaysCount()}">${reportData.getTrip(td, bd).price}</a></td>
				<#else>
                    <td align="center" style="background-color:#FFFFF">&nbsp;</td>
				</#if>
			</#list>
		</tr>
		</#list>
	</tbody>
	</table>
	
	<br>
	Report date: ${reportData.reportDate}.<br>
	<br>
	Time to generate this table is ${reportData.totalSearchTime}. Total number of flights within the table is ${reportData.tripsNumber}. Average time to process a flight is ${reportData.avgSearchTime}. Minimal time to process a flight is ${reportData.minSearchTime} and the maximal is ${reportData.maxSearchTime}.<br>
	<br>
	Minimal price is ${reportData.minimalPrice}. Maximal is ${reportData.maximalPrice}.<br>
	<br>	

</body>
</html>
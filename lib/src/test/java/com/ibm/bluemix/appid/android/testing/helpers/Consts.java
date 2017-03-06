/*
	Copyright 2017 IBM Corp.
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.ibm.bluemix.appid.android.testing.helpers;

public class Consts {
	public static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpPU0UifQ.eyJpc3MiOiJpbWYtYXV0aHNlcnZlci5zdGFnZTEubXlibHVlbWl4Lm5ldCIsImV4cCI6MTQ4OTk1NzQ1OSwiYXVkIjoiNDA4ZWIzNmEyYTA2OWFkODljZDE5Yzc4OWE5NmI3Y2YzNmI1NTBlYyIsInN1YiI6IjA5YjdmZWE1LTJlNGUtNDBiOC05ZDgxLWRmNTAwNzFhMzA1MyIsImFtciI6WyJmYWNlYm9vayJdLCJpYXQiOjE0ODczNjU0NTksInRlbmFudCI6IjUwZDBiZWVkLWFkZDctNDhkZC04YjBhLWM4MThjYjQ1NmJiNCIsInNjb3BlIjoiYXBwaWRfZGVmYXVsdCBhcHBpZF9yZWFkcHJvZmlsZSBhcHBpZF9yZWFkdXNlcmF0dHIgYXBwaWRfd3JpdGV1c2VyYXR0ciJ9.gQq4_IxbkPg1FsVZiiTqsejURL4E_Ijr8U1vDob-06GcsorVijS7HHf0kgWD84cDNa6z4Lp7HkmvI8vmiUIfV6ch-xJS_LSJphKy5nZxXqVHchRDJAMUNMiAYqC5ohZ4MXmjuGFIrVl1iZdTyP5Oz-5e6UzDccdAGkPokNs_IyXwiSmGWF5fOKSgfqANYwRBaC-JeXlzEcVZ697q92kiErBNl3ziuSFWxss86ZHHiKdLoHUpkDRKgPHwSQmE_Kwzj8v8Td9WuIVwXCF-D4koTuPJSe2aPqCLuV28PE9wRh5j3sFraKbQIcjuHuiAd5KBhzwaeVT20_0zrgyr3QG0Vg";
	public static final String ID_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpPU0UifQ.eyJpc3MiOiJpbWYtYXV0aHNlcnZlci5zdGFnZTEubXlibHVlbWl4Lm5ldCIsImF1ZCI6IjQwOGViMzZhMmEwNjlhZDg5Y2QxOWM3ODlhOTZiN2NmMzZiNTUwZWMiLCJleHAiOjE0ODk5NTc0NTksInRlbmFudCI6IjUwZDBiZWVkLWFkZDctNDhkZC04YjBhLWM4MThjYjQ1NmJiNCIsImlhdCI6MTQ4NzM2NTQ1OSwiZW1haWwiOiJkb25sb25xd2VydHlAZ21haWwuY29tIiwibmFtZSI6IkRvbiBMb24iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zY29udGVudC54eC5mYmNkbi5uZXQvdi90MS4wLTEvcDUweDUwLzEzNTAxNTUxXzI4NjQwNzgzODM3ODg5Ml8xNzg1NzY2MjExNzY2NzMwNjk3X24uanBnP29oPTE0OGQyZWVlNjRiYjE0YWZjZDg5MWIyZDVjMWQ2Zjg2Jm9lPTU5MkYzRUJDIiwic3ViIjoiMDliN2ZlYTUtMmU0ZS00MGI4LTlkODEtZGY1MDA3MWEzMDUzIiwiaWRlbnRpdGllcyI6W3sicHJvdmlkZXIiOiJmYWNlYm9vayIsImlkIjoiMzc3NDQwMTU5Mjc1NjU5In1dLCJhbXIiOlsiZmFjZWJvb2siXSwib2F1dGhfY2xpZW50Ijp7Im5hbWUiOiJhcHBpZCIsInR5cGUiOiJtb2JpbGVhcHAiLCJzb2Z0d2FyZV9pZCI6ImNvbS5pYm0ubW9iaWxlZmlyc3RwbGF0Zm9ybS5hcHBpZCIsInNvZnR3YXJlX3ZlcnNpb24iOiIxLjAiLCJkZXZpY2VfaWQiOiJlZWUyYzc4ZC0wZjEyLTM4MDgtOTFlYi1jNjM0NzVkYmJmOTUiLCJkZXZpY2VfbW9kZWwiOiJHVC1JOTUwMCIsImRldmljZV9vcyI6ImFuZHJvaWQifX0.Iy0l7C5mT8vum46G8Depk4KRXmOyBlJSWTRoPP41cXztSAqwOEZOXo4IWJVnwia46UbRgJ751VYZId2KTGap8H8R1sT-DkB8o27k8aIUT8dp0oNdnnjBYZR5sI5FLaqGJ02g8oddTlx2Dhb_XxZ4GwtfDCXLvIPgi3Q-1GrPjWNWOMP279KuBpy1a5KfOspQXp69rTaMJFXBzTo2ekVCKhx1mKwLRMWaE4RWkcwtl880lH6Nutz9B0ZneFrFl9MdNYH4y4BpWCUZKDobqgDl7kZFeSg5Zj8knOdlieDgevNKqMXAFERnV6q5pg2xgg5r-uvrjl7dg4Hol7j_MyTp8w";
}
package com.infosupport.service;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.infosupport.queue.QueueList;

@Path("/queue")
public class QueueService {
	
	private final String UPLOADED_FILE_PATH = "C://Users/MikeH/Pictures/Upload_Files/";

	@GET
	public Response getQueueSize() {
		if (QueueList.sizeOfQueue() > 0) {
			// OK 200
			return Response.status(200).build();
		} else {
			// Partial Information 203
			return Response.status(203).build();
		}
	}
	
	@GET
	@Path("/image")
	@Produces("image/png")
	public Response getImage() {
		if (QueueList.sizeOfQueue() > 0) {
			File file = new File(UPLOADED_FILE_PATH + QueueList.retrieveFromQueue().getRandomNr() + ".jpg");
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=image_from_server.jpg");
			return response.build();
		} else {
			return Response.status(203).build();
		}
	}
	
	@GET
	@Path("/description")
	public Response getDescription() {
		if (QueueList.sizeOfQueue() > 0) {
			return Response.ok((Object) QueueList.retrieveFromQueueButDontDelete().getDescription()).build();
		} else {
			return Response.status(203).build();
		}
	}

	@GET
	@Path("/location")
	public Response getLocation() {
		if (QueueList.sizeOfQueue() > 0) {
			return Response.ok((Object) QueueList.retrieveFromQueueButDontDelete().getLocation()).build();
		} else {
			return Response.status(203).build();
		}
	}
}

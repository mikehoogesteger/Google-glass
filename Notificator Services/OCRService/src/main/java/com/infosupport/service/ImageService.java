package com.infosupport.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.infosupport.queue.QueueItem;
import com.infosupport.queue.QueueList;

@Path("/image")
public class ImageService {

	private final String UPLOADED_FILE_PATH = "C://Users/MikeH/Pictures/Upload_Files/";

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadFile(MultipartFormDataInput input) {
		String fileName = "";
		
		File image = null;
		String description = null;
		String location = null;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("image");
		List<InputPart> descriptions = uploadForm.get("description");
		List<InputPart> locations = uploadForm.get("location");
		
		try {
			description = descriptions.get(0).getBodyAsString();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NullPointerException e) {
			
		}
		
		try {
			location = locations.get(0).getBodyAsString();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NullPointerException e) {
			
		}

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class,
						null);

				byte[] bytes = IOUtils.toByteArray(inputStream);
				
				String id = Integer.toString((int) new Date().getTime());

				// constructs upload file path
				fileName = UPLOADED_FILE_PATH + id + ".jpg";
				System.out.println(fileName);

				image = writeFile(bytes, fileName);
				QueueItem queueItem = new QueueItem(description, location, image, id);
				QueueList.addToQueue(queueItem);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return Response
				.status(200)
				.entity("uploadFile is called, Uploaded file name : "
						+ fileName).build();

	}

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition")
				.split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// save to somewhere
	private File writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

		return file;
	}

}

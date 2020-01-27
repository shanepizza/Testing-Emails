import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class FetchingEmail {
	
	public static void fetch(String pop3Host, String storeType, String user, String password) {
		
		try {
		//Create Properties field
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "pop3");
			properties.put("mail.pop3.host", pop3Host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);
			//EmailSession.setDebug(true); --- What is this???
			
		//Create the POP3 store object and connect with the pop server
			Store store = emailSession. getStore("pop3s");
			store.connect(pop3Host, user, password);
			
		//Create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
		//Retrieve the messages from the folder in an array and print it 
			Message[] messages = emailFolder.getMessages();
			System.out.println("Messages.length---" + messages.length);
			
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				System.out.println("---------------------------------------------");
				String line = reader.readLine();
				if ("YES".equals(line)) {
					message.writeTo(System.out);
				} else if ("QUIT".equals(line)) {
					break;
				}//End ifElse
			}//End For loop
			
		//Close the store and folder objects
			emailFolder.close(false);
			store.close();
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}//End Try Catch
	}//End Fetch method
	
	public static void main(String[] args) {
		String host = "pop.gmail.com";
		String mailStoreType = "pop3";
		String username = "adamjcarling@gmail.com";
		String password = "videogamehigh";
		
	//call method Fetch
		fetch(host,mailStoreType, username, password);
	}//End Main Method
	
	/*
	 * This method checks for content-type
	 * based on which, it processes and 
	 * fetches the content of the message
	 */
	public static void writePart(Part p) throws Exception {
		if (p instanceof Message)
			//Call method writeEnvelope
			writeEnvelope((Message) p);
		
		
		System.out.println("-----------------------------------------------------");
		System.out.println("CONTENT_TYPE: " + p.getContentType());
		
	//Check if the content is plain text
		if(p.isMimeType("text/plain")) {
			System.out.println("This is plain text");
			System.out.println("-------------------------");
			System.out.println((String) p.getContent());
		}//End if
		
		//Check if the Content has attachment
		else if (p.isMimeType("Multipart/*")) {
			System.out.println("This is a Multipart");
			System.out.println("-------------------------");
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				writePart(mp.getBodyPart(i));
			}//End for loop
		}//End if else
		
	//Check if the content is a nested Message
		else if (p.isMimeType("Message/rfc822")) {
			System.out.println("This is a Nested Message");
			System.out.println("--------------------------");
			writePart((Part) p.getContent());
		}//End Else If for Nested Message
	
		
	//Check if content is an inline Image
		else if (p.isMimeType("image/jpeg")){
			/*System.out.println("--------> image/jpeg");
			Object o = p.getContent();
			
			InputStream x = (InputStream) o;
		//Constuct the required byte array
			System.out.println("x.length = " + x.available());
			
			while ((i = (int) ((InputStream) x).available()) > 0) {
				int result = (int) (((InputStream) x).read(bArray));
				if (result == -1)
			int i = 0;
			byte[] bArray = new byte[x.available()];
				break;
			}//End of while loop
			
			FileOutputStream f2 = new FileOutputStream("/tmp/image.jpeg");
			f2.write(bArray);*/
			System.out.println("This is an In-Line Image. We are having trouble with these.");
		}//End ELSE IF for inline Imgae
		
		else if (p.getContentType().contains("image")) {
			System.out.println("content type" + p.getContentType());
			File f = new File("image" + new Date().getTime() + ".jpg");
			DataOutputStream output = new DataOutputStream( new BufferedOutputStream(new FileOutputStream(f)));
			com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = test.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}//End While Loop
		}//End else if for Normal Image?
		else {
			Object o = p.getContent();
			if (o instanceof String) {
				System.out.println("This is a string");
				System.out.println("------------------------------------");
				System.out.println((String) o);
			}//End IF
			else if (o instanceof InputStream) {
				System.out.println("This is just an input stream");
				System.out.println("-------------------------------------");
				InputStream is = (InputStream) o;
				is = (InputStream) o;
				int c;
				while ((c = is.read()) != -1)
					System.out.write(c);
			}//End INPUTSTREAM IF
			else {
				System.out.println("This is an unkown type");
				System.out.println("--------------------------------------");
				System.out.println(o.toString());
			}//End another Else	
		}//End Last Else
		
	}//End Method writePart
	
/*
 * This method would print From, TO and SUBJECT of the message
 */
	public static void writeEnvelope(Message m) throws Exception {
		System.out.println("This is the message envelope");
		System.out.println("----------------------------");
		Address[] a;
		
	//FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++)
				System.out.println("FROM: " + a[j].toString());
		}//End null IF
		
	//TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++)
				System.out.println("TO: " + a[j].toString());
		}//End TO null IF
		
	//SUBJECT
		if (m.getSubject() != null)
			System.out.println("SUBJECT: " + m.getSubject());
	
	}//End writeEnvelope method
}//end FetchingEmail class

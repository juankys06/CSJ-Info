package com.solicitud.solicitud.mail;

import java.io.File;
import java.util.Map;

public class Mail {
	    private String from;
	    private String[] to;
	    private String [] cc;
	    private String subject;
	    private String content;
	    private Map<String, Object> model;
	    private File file;


		public Mail() {
	    }

	    public Mail(String from, String[] to, String [] cc, String subject, String content, Map<String, Object> model) {
			this.from = from;
			this.to = to;
			this.cc = cc;
			this.subject = subject;
			this.content = content;
			this.model = model;
		}

		public String getFrom() {
	        return from;
	    }

	    public void setFrom(String from) {
	        this.from = from;
	    }

	    public String[] getTo() {
	        return to;
	    }

        public void setTo(String to) {
            this.to = to.split(",");
        }
        
        public void setTo(String[] to) {
            this.to = to;
        }
	    
	    public String [] getCc() {
	        return cc;
	    }

	    public void setCc(String [] cc) {
	        this.cc = cc;
	    }

	    public String getSubject() {
	        return subject;
	    }

	    public void setSubject(String subject) {
	        this.subject = subject;
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }

	    @Override
	    public String toString() {
	        return "Mail{" +
	                "from='" + from + '\'' +
	                ", to='" + to + '\'' +
	                ", subject='" + subject + '\'' +
	                ", content='" + content + '\'' +
	                '}';
	    }

		public Map<String, Object> getModel() {
			return model;
		}

		public void setModel(Map<String, Object> model) {
			this.model = model;
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}
		

	    
}

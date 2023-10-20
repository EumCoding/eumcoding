package com.latteis.eumcoding.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

@Component
public class FileUploadProgressListener implements HttpSessionBindingListener {

    private HttpSession session;

    public FileUploadProgressListener() {}

    public FileUploadProgressListener(HttpSession session) {
        this.session = session;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        session = event.getSession();
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        session = null;
    }

    public void update(long bytesRead, long contentLength, int items) {
        ProgressEntity status = new ProgressEntity();
        status.setBytesRead(bytesRead);
        status.setContentLength(contentLength);
        session.setAttribute("status", status);
    }
}
package dev.vality.deanonimus.endpoint;

import dev.vality.damsel.deanonimus.DeanonimusSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/deanonimus")
public class DeanonimusServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private DeanonimusSrv.Iface deanonimusServiceHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(DeanonimusSrv.Iface.class, deanonimusServiceHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}

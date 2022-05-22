package ch.martinelli.vaadin.demo.views.helloworld;

import ch.martinelli.vaadin.demo.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView extends VerticalLayout {

    public HelloWorldView() {
        setMargin(true);

        var name = new TextField("Your name");
        name.setId("name");

        var sayHello = new Button("Say hello");
        sayHello.setId("say-hello");

        var text = new Label();
        text.setId("text");

        sayHello.addClickListener(e -> text.setText("Hello " + name.getValue()));

        FormLayout formLayout = new FormLayout(name, sayHello);
        formLayout.setWidth("50%");

        add(formLayout, text);
    }

}

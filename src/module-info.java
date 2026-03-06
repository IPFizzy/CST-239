module CST239Milestone {
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Jackson uses reflection to create objects, so the model package must be opened.
    opens model to com.fasterxml.jackson.databind;
}
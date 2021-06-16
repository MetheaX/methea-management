package io.github.metheax.mgt.data.service;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.helpers.CrudService;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Kuylim TITH
 * Date: 6/16/2021
 */
public class CustomCrudServiceDataProvider<T, F> extends FilterablePageableDataProvider<T, F> {

    private final CrudService<T, String> service;
    private List<QuerySortOrder> defaultSortOrders;

    public CustomCrudServiceDataProvider(CrudService<T, String> service, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    @Override
    protected Page<T> fetchFromBackEnd(Query<T, F> query, Pageable pageable) {
        return this.service.list(pageable);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return this.defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        return this.service.count();
    }
}

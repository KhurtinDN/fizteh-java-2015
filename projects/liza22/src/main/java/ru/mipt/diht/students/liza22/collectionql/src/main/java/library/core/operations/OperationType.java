package library.core.operations;

public enum OperationType {
    WHERE_OP                (1),
    SIMPLE_SELECT_OP        (2),
    GROUPING_SELECT_OP      (2),
    HAVING_OP               (3),
    ORDER_BY_OP             (4),
    LIMIT_OP                (5),
    DISTINCT_OP             (6),
    UNION_OP                (10);

    /**
     * Order of operation to be invoked in a query execution sequence.
     */
    private int order;

    OperationType(int order1) {
        this.order = order1;
    }

    public int getOrder() {
        return order;
    }


}

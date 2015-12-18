package library.core.operations.impl;

import library.core.exceptions.IncorrectQueryException;
import library.core.model.GroupingCondition;
import library.core.model.SelectArgument;
import library.core.operations.OperationType;
import library.core.QueryContext;

import java.util.*;

/**
 * This type of operation used when select query is grouping.
 * Some additional preparations are required before do select
 * for each group of elements.
 */
public final class GroupingSelectOperation extends AbstractSelectOperation {

    @Override
    public  OperationType getType() {
        return OperationType.GROUPING_SELECT_OP;
    }

    @Override
    public <R, S> void validate(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        super.validate(queryContext);
        // check that select arguments consist of aggregate functions or grouping conditions only
        List<SelectArgument<S>> selectArguments = queryContext.getSelectArguments();
        List<GroupingCondition<S>> groupingConditions = queryContext.getGroupingConditions();
        long aggregateFunctionsCnt = selectArguments.stream().filter(SelectArgument::isAggregate).count();
        long simpleFunctionsCnt = selectArguments.size() - aggregateFunctionsCnt;
        if (simpleFunctionsCnt != groupingConditions.size()) {
            throw new IncorrectQueryException("The only grouping expressions or aggregate functions "
                    + "are permitted in grouping select query.");
        }
    }

    @Override
    public  <R, S> void execute(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        List<S> sourceElements = queryContext.getSource();
        // in case when all rows have been filtered out
        if (sourceElements.isEmpty()) {
            return;
        }

        // Step#1: calculate grouping value for each source element
        List<GroupingCondition<S>> groupingConditions = queryContext.getGroupingConditions();
        calculateGroupingConditionValues(sourceElements, groupingConditions);

        // Step#2: grouping source elements
        Map<String, List<S>> groupsOfElements = groupElementsByConditions(sourceElements, groupingConditions);

        // Step#3: calculate select argument for each group of elements separately
        for (String groupKey : groupsOfElements.keySet()) {
            List<S> groupElements = groupsOfElements.get(groupKey);
            List<R> groupResults = doSelect(groupElements, queryContext);
            queryContext.addResult(groupResults);
        }
    }

    private <S> Map<String, List<S>> groupElementsByConditions(List<S> Elements, List<GroupingCondition<S>> condition) {
        // for each source element the groupKey is calculated
        Map<String, List<S>> groups = new HashMap<>();
        for (S element : Elements) {
            String groupKey = calculateGroupKeyOfElement(element, condition);
            List<S> groupElements = groups.get(groupKey);
            if (groupElements == null) {
                groupElements = new ArrayList<>();
                groups.put(groupKey, groupElements);
            }
            groupElements.add(element);
        }
        return groups;
    }

    /*
     * GroupKey consists of hashcodes of condition values for this element
     * like "84141-14515-6111" in case of 3 grouping conditions and
     * hashcode(condition_1_value) = 84141, hashcode(condition_2_value) = 14515, hashcode(condition_3_value) = 6111
     * elements with same groupKey belongs to the same group
     *
     * @param element source element for which groupKey has to be calculated
     * @param conditions list of groupBy conditions
     * @param <S> source element type
     * @return calculated GroupKey for this element
     */
    private static <S> String calculateGroupKeyOfElement(S element, List<GroupingCondition<S>> conditions) {
        StringBuilder groupKeyBuilder = new StringBuilder();
        boolean first = true;
        for (GroupingCondition<S> condition : conditions) {
            if (!first) { groupKeyBuilder.append("-"); }
            else { first = false; }

            Map<S, Object> values = condition.getGroupedValues();
            Object elValue = values.get(element);
            groupKeyBuilder.append(elValue.hashCode());
        }
        return groupKeyBuilder.toString();
    }

    private <S> void calculateGroupingConditionValues(List<S> Elements, List<GroupingCondition<S>> groupingConditions) {
        for (GroupingCondition<S> condition : groupingConditions) {
            Map<S, Object> elementValues = new HashMap<>(Elements.size());
            for (S element : Elements) {
                Object groupedValue = condition.getFunction().apply(element);
                elementValues.put(element, groupedValue);
            }
            condition.setGroupedValues(elementValues);
        }
    }
}

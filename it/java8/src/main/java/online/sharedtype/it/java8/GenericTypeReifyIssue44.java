package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

/**
 * InterfaceA can have different type argument values.
 *
 * @author Cause Chung
 */
public class GenericTypeReifyIssue44 {
    interface GenericInterface<T> {
        T getValue();
    }
    interface GenericInterfaceNoNeedToImplement<T> {
        default T noNeedToImplement() {
            return null;
        }
    }

    @SharedType
    static class SubtypeWithString implements GenericInterface<String> {
        @Override
        public String getValue() {
            return "Generic type reified as String";
        }
    }

    @SharedType
    static class SubtypeWithInteger implements GenericInterface<Integer> {
        @Override
        public Integer getValue() {
            return 44;
        }
    }

    @SharedType
    static class SubtypeWithStringArray implements GenericInterface<String[]> {
        @Override
        public String[] getValue() {
            return new String[0];
        }
    }

    @SharedType
    static class SubtypeWithIntegerArray implements GenericInterface<Integer[]> {
        @Override
        public Integer[] getValue() {
            return new Integer[0];
        }
    }

    @SharedType
    static class SubtypeWithNestedString implements GenericInterface<GenericInterface<String>> {
        @Override
        public GenericInterface<String> getValue() {
            return null;
        }
    }

    @SharedType
    static class SubtypeWithNestedInteger implements GenericInterface<GenericInterface<Integer>> {
        @Override
        public GenericInterface<Integer> getValue() {
            return null;
        }
    }

    static final class CustomContainer<T> {
        int value;
    }

    @SharedType
    static class SubtypeWithNestedCustomTypeString implements GenericInterface<CustomContainer<String>>, GenericInterfaceNoNeedToImplement<CustomContainer<String>> {
        @Override
        public CustomContainer<String> getValue() {
            return null;
        }
    }

    @SharedType
    static class SubtypeWithNestedCustomTypeInteger implements GenericInterface<CustomContainer<Integer>>, GenericInterfaceNoNeedToImplement<CustomContainer<Integer>> {
        @Override
        public CustomContainer<Integer> getValue() {
            return null;
        }
    }
}

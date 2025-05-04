mod types;

pub static CUSTOM_CODE_TYPE: types::CustomInjectedStruct = types::CustomInjectedStruct {
    field: 33,
};

#[cfg(test)]
mod tests {
    use std::collections::HashMap;

    use super::types::*;

    #[test]
    fn cyclic_dependencies() {
        let dep_a = DependencyClassA {
            b: Some(Box::new(DependencyClassB {
                c: Some(Box::new(DependencyClassC {
                    a: Some(Box::new(DependencyClassA {
                        b: None,
                        a: 6,
                        value: 33,
                        notIgnoredImplementedMethod: 999,
                    })),
                })),
            })),
            a: 5,
            value: 4,
            notIgnoredImplementedMethod: 5,
        };
        let json = serde_json::to_string(&dep_a).unwrap();

        let dep_a_deser: DependencyClassA = serde_json::from_str(&json).unwrap();
        assert_eq!(dep_a_deser, dep_a);

        print!("{}", &json);
        assert_eq!(
            &json,
            r#"{"b":{"c":{"a":{"b":null,"a":6,"value":33,"notIgnoredImplementedMethod":999}}},"a":5,"value":4,"notIgnoredImplementedMethod":5}"#
        );
    }

    #[test]
    fn recursieve_type() {
        let recusive_class = RecursiveClass {
            directRef: Some(Box::new(RecursiveClass {
                directRef: None,
                arrayRef: vec![],
            })),
            arrayRef: vec![Box::new(RecursiveClass {
                directRef: None,
                arrayRef: vec![],
            })],
        };

        let json = serde_json::to_string(&recusive_class).unwrap();

        let recusive_class_deser: RecursiveClass = serde_json::from_str(&json).unwrap();
        assert_eq!(recusive_class_deser, recusive_class);

        print!("{}", &json);
        assert_eq!(
            &json,
            r#"{"directRef":{"directRef":null,"arrayRef":[]},"arrayRef":[{"directRef":null,"arrayRef":[]}]}"#
        );
    }

    #[test]
    fn map_class() {
        let mut map_class = MapClass {
            mapField: HashMap::new(),
            enumKeyMapField: HashMap::new(),
            customMapField: HashMap::new(),
            nestedMapField: HashMap::new(),
        };

        map_class.mapField.insert(33, String::from("v1"));
        map_class
            .nestedMapField
            .insert(String::from("m1"), HashMap::new());

        let json = serde_json::to_string(&map_class).unwrap();

        let map_class_deser: MapClass = serde_json::from_str(&json).unwrap();
        assert_eq!(map_class_deser, map_class);

        print!("{}", &json);
        assert_eq!(
            &json,
            r#"{"mapField":{"33":"v1"},"enumKeyMapField":{},"customMapField":{},"nestedMapField":{"m1":{}}}"#
        );
    }

    #[test]
    fn math_classes() {
        let _: MathClass = MathClass {
            bigInteger: String::from("500000000"),
            bigDecimal: String::from("500000000.123456789"),
        };
    }

    #[test]
    fn constants() {
        assert_eq!(STATIC_FIELD_FROM_JAVA_RECORD, 888);
        assert_eq!(FLOAT_VALUE, 1.888);
        assert_eq!(LONG_VALUE, 999);
        assert_eq!(MATH_VALUE, "1.1");

        assert_eq!(MyEnumConstants::INT_VALUE, 1);
        assert_eq!(MyEnumConstants::STR_VALUE, "abc");
    }

    #[test]
    fn optional_methods() {
        let mut optional_methods: OptionalMethod = OptionalMethod {
            valueOptional: None,
            nestedValueOptional: None,
            setNestedValueOptional: None,
            mapNestedValueOptional: None,
        };
        optional_methods.mapNestedValueOptional =
            Some(HashMap::from_iter([(1, String::from("foo"))]));
        optional_methods.valueOptional = Some(String::from("foo"));

        let json = serde_json::to_string(&optional_methods).unwrap();

        let optional_methods_deser: OptionalMethod = serde_json::from_str(&json).unwrap();
        assert_eq!(optional_methods_deser, optional_methods);

        print!("{}", &json);
        assert_eq!(
            &json,
            r#"{"valueOptional":"foo","nestedValueOptional":null,"setNestedValueOptional":null,"mapNestedValueOptional":{"1":"foo"}}"#
        );
    }

    #[test]
    fn enum_values() {
        assert_eq!(EnumTShirt::S.value(), "S");
        assert_eq!(EnumSize::LARGE.value(), 3);
        assert_eq!(EnumConstReference::ReferenceConstantInOther.value(), 999);
        assert_eq!(EnumConstReference::ReferenceConstantLocally.value(), 156);
        assert_eq!(EnumEnumReference::ReferenceAnother.value(), 3);
        assert_eq!(EnumSimpleEnumReference::ReferenceAnother.value(), EnumGalaxy::Andromeda);
        assert_eq!(EnumEnumEnumReference::ReferenceAnother2.value(), EnumGalaxy::Andromeda);
    }
}

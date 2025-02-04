mod types;

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
        assert_eq!(&json, r#"{"b":{"c":{"a":{"b":null,"a":6,"value":33,"notIgnoredImplementedMethod":999}}},"a":5,"value":4,"notIgnoredImplementedMethod":5}"#);
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
        assert_eq!(&json, r#"{"directRef":{"directRef":null,"arrayRef":[]},"arrayRef":[{"directRef":null,"arrayRef":[]}]}"#);
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
        map_class.nestedMapField.insert(String::from("m1"), HashMap::new());

        let json = serde_json::to_string(&map_class).unwrap();

        let map_class_deser: MapClass = serde_json::from_str(&json).unwrap();
        assert_eq!(map_class_deser, map_class);

        print!("{}", &json);
        assert_eq!(&json, r#"{"mapField":{"33":"v1"},"enumKeyMapField":{},"customMapField":{},"nestedMapField":{"m1":{}}}"#);
    }
}

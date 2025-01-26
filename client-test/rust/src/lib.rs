mod types;

#[cfg(test)]
mod tests {
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
                      not_ignored_implemented_method: 999,
                    })),
                })),
            })),
            a: 5,
            value: 4,
            not_ignored_implemented_method: 5,
        };
        let json = serde_json::to_string(&dep_a).unwrap();

        let dep_a_deser: DependencyClassA = serde_json::from_str(&json).unwrap();
        assert_eq!(dep_a_deser, dep_a);

        print!("{}", &json);
        assert_eq!(&json, r#"{"b":{"c":{"a":{"b":null,"a":6,"value":33,"not_ignored_implemented_method":999}}},"a":5,"value":4,"not_ignored_implemented_method":5}"#);
    }

    #[test]
    fn recursieve_type() {
        let recusive_class = RecursiveClass {
            direct_ref: Some(Box::new(RecursiveClass {
                direct_ref: None,
                array_ref: vec![],
            })),
            array_ref: vec![Box::new(RecursiveClass {
                direct_ref: None,
                array_ref: vec![],
            })],
        };

        let json = serde_json::to_string(&recusive_class).unwrap();

        let recusive_class_deser: RecursiveClass = serde_json::from_str(&json).unwrap();
        assert_eq!(recusive_class_deser, recusive_class);

        print!("{}", &json);
        assert_eq!(&json, r#"{"direct_ref":{"direct_ref":null,"array_ref":[]},"array_ref":[{"direct_ref":null,"array_ref":[]}]}"#);
    }
}

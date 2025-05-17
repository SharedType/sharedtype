// test code snippet
pub struct CustomInjectedStruct {
    pub field: i32,
}

fn serialize_any<S>(value: &Box<dyn Any>, serializer: S) -> Result<S::Ok, S::Error>
where
    S: serde::Serializer,
{
    if let Some(value) = value.downcast_ref::<String>() {
        serializer.serialize_str(value)
    } else if let Some(value) = value.downcast_ref::<i32>() {
        serializer.serialize_i32(*value)
    } else {
        Err(serde::ser::Error::custom("Unsupported type"))
    }
}
fn deserialize_any<'de, D>(deserializer: D) -> Result<Box<dyn Any>, D::Error>
where
    D: serde::Deserializer<'de>,
{
    let s: &str = serde::Deserialize::deserialize(deserializer)?;
    Ok(Box::new(s.to_string()))
}

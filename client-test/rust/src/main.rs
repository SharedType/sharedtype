mod types;

use std::io::Cursor;
use tiny_http::{Method, Response, Server, StatusCode};
use types::{ArrayClass, DependencyClassA, JavaClass, JavaTimeClass, MapClass, MathClass, SubtypeWithNestedCustomTypeString};

type JavaRecord = types::JavaRecord<String>;

#[derive(Debug, Clone)]
struct ResponseValue {
    body: String,
    status: u16,
}
impl From<ResponseValue> for Response<Cursor<String>> {
    fn from(value: ResponseValue) -> Self {
        Response::new(
            StatusCode::from(value.status),
            vec![],
            Cursor::new(value.body),
            None,
            None,
        )
    }
}

macro_rules! parse_obj {
    ( $path:expr, $content:expr, $( $t:ty ),* ) => {
        match $path {
            $(
                concat!("/", stringify!($t)) => {
                    let res = serde_json::from_str::<$t>($content);
                    match res {
                        Ok(obj) => Some(serde_json::to_string(&obj).unwrap()),
                        Err(e) => {
                            println!("Error parsing {}: {:?}, content: {}", stringify!($t), e, $content);
                            None
                        }
                    }
                }
            )*
            _ => None,
        }
    };
}

fn main() {
    let not_found_response_value: ResponseValue = ResponseValue {
        body: "Unknown path".to_string(),
        status: 404,
    };
    let ok_response_value: ResponseValue = ResponseValue {
        body: "ok".to_string(),
        status: 200,
    };
    let empty_body_response_value: ResponseValue = ResponseValue {
        body: "Empty body".to_string(),
        status: 400,
    };

    let server = Server::http("0.0.0.0:3002").unwrap();
    println!("Server listening on port 3002");

    for mut request in server.incoming_requests() {
        let request_method = request.method().clone();
        let request_url = request.url().to_string();

        let response_value = match request_url.as_str() {
            "/health" if request_method == Method::Get => ok_response_value.clone(),
            path if request_method == Method::Post => {
                let mut content = String::new();
                request.as_reader().read_to_string(&mut content).unwrap();
                if content.len() == 0 {
                    empty_body_response_value.clone()
                } else {
                    let json_opt = {
                        let path = path;
                        let content: &str = &content;
                        parse_obj!(
                            path,
                            content,
                            JavaClass,
                            JavaTimeClass,
                            SubtypeWithNestedCustomTypeString,
                            DependencyClassA,
                            MapClass,
                            ArrayClass,
                            JavaRecord,
                            MathClass
                        )
                    };
                    match json_opt {
                        Some(json) => ResponseValue {
                            body: json,
                            status: 200,
                        },
                        None => not_found_response_value.clone(),
                    }
                }
            }
            _ => not_found_response_value.clone(),
        };

        println!(
            "{:?} {:?} - {:?} : {}",
            response_value.status, request_method, request_url, response_value.body
        );

        let res = request.respond(Response::from(response_value));
        if res.is_err() {
            println!("Error responding to request: {:?}", res.err());
        }
    }
}

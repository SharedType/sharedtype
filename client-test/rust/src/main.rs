mod types;

use std::io::Cursor;
use tiny_http::{Method, Response, Server, StatusCode};
use types::JavaClass;

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

    let server = Server::http("localhost:3002").unwrap();
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
                    let json_opt = route_obj(path, &content);
                    match json_opt {
                        Some(json) => {
                            ResponseValue {
                                body: json,
                                status: 200,
                            }
                        }
                        None => not_found_response_value.clone()
                    }
                }
            }
            _ => not_found_response_value.clone(),
        };

        println!(
            "{:?} {:?} - {:?} : {:?}",
            response_value.status, request_method, request_url, response_value.body
        );

        let res = request.respond(Response::from(response_value));
        if res.is_err() {
            println!("Error responding to request: {:?}", res.err());
        }
    }
}

fn route_obj(path: &str, content: &str) -> Option<String> {
    let obj_opt = match path {
        "/JavaClass" => {
            let obj: JavaClass = serde_json::from_str(content).unwrap();
            Option::Some(obj)
        }
        _ => None,
    };
    obj_opt.map(|obj| serde_json::to_string(&obj).unwrap())
}

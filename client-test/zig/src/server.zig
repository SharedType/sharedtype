const std = @import("std");
const zap = @import("zap");

pub fn main() !void {
    var listener = zap.HttpListener.init(.{
        .port = 3008,
        .on_request = on_request,
        .log = true,
    });
    try listener.listen();

    std.debug.print("Listening on 0.0.0.0:3008\n", .{});

    // start worker threads
    zap.start(.{
        .threads = 1,
        .workers = 1,
    });
}

fn on_request(r: zap.Request) !void {
    if (r.path) |the_path| {
        std.debug.print("PATH: {s}\n", .{the_path});
    }

    if (r.query) |the_query| {
        std.debug.print("QUERY: {s}\n", .{the_query});
    }
    r.sendBody("<html><body><h1>Hello from ZAP!!!</h1></body></html>") catch return;
}

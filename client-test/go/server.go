package main

import (
	"encoding/json"
	"fmt"
	"log/slog"
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"sharedtype.online/go-client-test/sharedtype"
)

func main() {
	r := chi.NewRouter()
	r.Use(middleware.Logger)
	r.Get("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte(`ok`))
	})
	r.Post("/{typeName}", route)
	fmt.Println("Server started on :3001, waiting for requests...")
	http.ListenAndServe(":3001", r)
	defer fmt.Println("Server stopped.")
}

func route(w http.ResponseWriter, r *http.Request) {
	typeName := chi.URLParam(r, "typeName")
	res := createStruct(typeName)

	if res == nil {
		http.Error(w, fmt.Sprintf("Unknown typeName: %s", typeName), 400)
		return
	}

	if err := json.NewDecoder(r.Body).Decode(res); err != nil {
		http.Error(w, err.Error(), 400)
		slog.Error("Failed to decode request body", "error", err)
		return
	}

	w.Header().Set("Content-Type", "application/json")

	if err := json.NewEncoder(w).Encode(res); err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
}

func createStruct(typename string) any {
	switch typename {
	case "JavaClass":
		return &sharedtype.JavaClass{}
	case "JavaTimeClass":
		return &sharedtype.JavaTimeClass{}
	case "SubtypeWithNestedCustomTypeString":
		return &sharedtype.SubtypeWithNestedCustomTypeString{}
	case "DependencyClassA":
		return &sharedtype.DependencyClassA{}
	case "MapClass":
		return &sharedtype.MapClass{}
	case "ArrayClass":
		return &sharedtype.ArrayClass{}
	case "JavaRecord":
		return &sharedtype.JavaRecord[any]{}
	case "MathClass":
		return &sharedtype.MathClass{}
	default:
		return nil
	}
}

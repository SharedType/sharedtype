package main

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"sharedtype.online/go-client-test/sharedtype"
)

func main() {
	r := chi.NewRouter()
	r.Use(middleware.Logger)
	r.Get("/", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte(`ok`))
	})
	r.Post("/{typeName}", route)
	fmt.Println("Server started on :3001, waiting for requests...")
	http.ListenAndServe(":3001", r)
	defer fmt.Println("Server stopped.")
}

func route(w http.ResponseWriter, r *http.Request) {
	typeName := chi.URLParam(r, "typeName")
	var res any
	switch typeName {
	case "SubtypeWithString":
		res = &sharedtype.SubtypeWithString{}
	case "JavaClass":
		res = &sharedtype.JavaClass{}
	}

	if res == nil {
		http.Error(w, fmt.Sprintf("Unknown typeName: %s", typeName), 400)
		return
	}

	if err := json.NewDecoder(r.Body).Decode(res); err != nil {
		http.Error(w, err.Error(), 400)
		return
	}

	w.Header().Set("Content-Type", "application/json")

	if err := json.NewEncoder(w).Encode(res); err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
}

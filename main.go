// main.go
package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"sync"
	"time"
)

type Task struct {
	ID      int
	Payload string
}

func worker(id int, tasks <-chan Task, results chan<- string, wg *sync.WaitGroup) {
	defer wg.Done()
	for task := range tasks {
		log.Printf("Worker %d starting task %d\n", id, task.ID)
		time.Sleep(100 * time.Millisecond) // simulate work
		res := fmt.Sprintf("Task %d processed by worker %d", task.ID, id)
		results <- res
		log.Printf("Worker %d completed task %d\n", id, task.ID)
	}
}

func main() {
	numWorkers, numTasks := 4, 20
	tasks := make(chan Task, numTasks)
	results := make(chan string, numTasks)
	var wg sync.WaitGroup

	// Launch workers
	for i := 1; i <= numWorkers; i++ {
		wg.Add(1)
		go worker(i, tasks, results, &wg)
	}

	// Enqueue tasks
	for i := 1; i <= numTasks; i++ {
		tasks <- Task{ID: i, Payload: fmt.Sprintf("Payload %d", i)}
	}
	close(tasks)

	// Wait for all workers to finish
	wg.Wait()
	close(results)

	// Write results to file
	file, err := os.Create("results_go.txt")
	if err != nil {
		log.Fatalf("Failed to create file: %v", err)
	}
	defer file.Close()
	writer := bufio.NewWriter(file)
	for res := range results {
		if _, err := writer.WriteString(res + "\n"); err != nil {
			log.Printf("Write error: %v", err)
		}
	}
	writer.Flush()
}
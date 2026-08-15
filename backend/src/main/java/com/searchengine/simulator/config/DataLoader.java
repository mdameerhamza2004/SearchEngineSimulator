package com.searchengine.simulator.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.simulator.model.Document;
import com.searchengine.simulator.model.LearningTopic;
import com.searchengine.simulator.repository.DocumentRepository;
import com.searchengine.simulator.service.DsaTopicService;
import com.searchengine.simulator.service.SearchEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final DocumentRepository documentRepository;
    private final SearchEngineService searchEngineService;
    private final DsaTopicService dsaTopicService;

    public DataLoader(DocumentRepository documentRepository, SearchEngineService searchEngineService, DsaTopicService dsaTopicService) {
        this.documentRepository = documentRepository;
        this.searchEngineService = searchEngineService;
        this.dsaTopicService = dsaTopicService;
    }

    @Override
    public void run(String... args) {
        if (documentRepository.count() > 0) {
            logger.info("Documents already exist in the database. Skipping data initialization.");
            return;
        }

        logger.info("Initializing sample dataset for Search Engine Simulator...");

        List<Document> documents = new ArrayList<>();

        // 1. Data Structures
        documents.add(new Document("Arrays and Dynamic Arrays", 
                "An array is a linear data structure that stores elements of the same type in contiguous memory locations. " +
                "Dynamic arrays, like ArrayList in Java, automatically resize themselves when elements are added beyond their capacity. " +
                "They offer O(1) time complexity for random access, making them highly efficient for read-heavy operations. " +
                "However, inserting or deleting elements in the middle can be slow, requiring O(n) time.", 
                "Data Structures"));
                
        documents.add(new Document("Linked Lists (Singly and Doubly)", 
                "A linked list is a linear data structure where elements, called nodes, are not stored in contiguous memory. " +
                "In a singly linked list, each node contains data and a reference to the next node. " +
                "A doubly linked list adds a reference to the previous node, allowing traversal in both directions. " +
                "They provide O(1) insertions and deletions if the node reference is known, but O(n) for random access.", 
                "Data Structures"));

        documents.add(new Document("Stacks and Queues", 
                "A stack is a linear data structure following the Last-In-First-Out (LIFO) principle, where elements are added and removed from the top. " +
                "A queue follows the First-In-First-Out (FIFO) principle, similar to a line of people waiting for service. " +
                "Both structures offer O(1) time complexity for their primary operations: push/pop for stacks, and enqueue/dequeue for queues. " +
                "They are fundamental in parsing algorithms, depth-first search, and breadth-first search.", 
                "Data Structures"));

        documents.add(new Document("Hash Tables and Hash Maps", 
                "Hash tables are data structures that store key-value pairs, providing incredibly fast data retrieval. " +
                "They use a hash function to compute an index into an array of buckets or slots, from which the desired value can be found. " +
                "On average, search, insertion, and deletion operations have O(1) time complexity. " +
                "Hash collisions, where different keys hash to the same index, are handled using chaining or open addressing.", 
                "Data Structures"));

        documents.add(new Document("Binary Trees and Binary Search Trees", 
                "A binary tree is a hierarchical data structure where each node has at most two children, referred to as the left and right child. " +
                "A Binary Search Tree (BST) is a variant where the left child contains values less than the parent, and the right child contains values greater. " +
                "This ordered property allows for efficient searching, insertion, and deletion, with O(log n) time complexity on average. " +
                "However, in the worst case (an unbalanced tree), operations can degrade to O(n) time complexity.", 
                "Data Structures"));

        documents.add(new Document("Graphs: Adjacency List vs Adjacency Matrix", 
                "A graph is a non-linear data structure consisting of nodes (vertices) connected by edges. " +
                "An adjacency matrix represents a graph using a 2D array, which is space-inefficient for sparse graphs but allows O(1) edge lookups. " +
                "An adjacency list uses an array of lists to store connections, making it highly memory-efficient for sparse graphs. " +
                "Choosing the right representation depends on the graph density and the specific graph algorithms being executed.", 
                "Data Structures"));

        // 2. Algorithms
        documents.add(new Document("Sorting Algorithms: QuickSort and MergeSort", 
                "QuickSort and MergeSort are two of the most popular divide-and-conquer sorting algorithms. " +
                "MergeSort divides the array into halves, sorts them, and merges them back in O(n log n) time, ensuring stable sorting. " +
                "QuickSort selects a 'pivot' element and partitions the array around it, offering excellent average-case performance. " +
                "While QuickSort is generally faster in practice due to cache locality, its worst-case time complexity is O(n^2).", 
                "Algorithms"));

        documents.add(new Document("Binary Search and its Variants", 
                "Binary search is an efficient algorithm for finding an element in a sorted array by repeatedly halving the search interval. " +
                "It has an O(log n) time complexity, making it much faster than a linear search for large datasets. " +
                "Variants include finding the first or last occurrence of a target, or finding the insertion point for a new element. " +
                "The algorithm is a classic example of the divide-and-conquer strategy and requires random access to data elements.", 
                "Algorithms"));

        documents.add(new Document("Dynamic Programming Fundamentals", 
                "Dynamic programming is a method for solving complex problems by breaking them down into simpler overlapping subproblems. " +
                "It optimizes recursion by storing the results of subproblems (memoization) or building up solutions from the bottom (tabulation). " +
                "Classic examples include computing the Fibonacci sequence, the Knapsack problem, and finding the longest common subsequence. " +
                "This technique drastically reduces the time complexity from exponential to polynomial for many optimization problems.", 
                "Algorithms"));

        documents.add(new Document("Graph Traversal: BFS and DFS", 
                "Breadth-First Search (BFS) and Depth-First Search (DFS) are foundational algorithms for traversing or searching graph data structures. " +
                "BFS explores all neighbor nodes at the present depth before moving deeper, typically using a queue. " +
                "DFS explores as far as possible along each branch before backtracking, commonly implemented using recursion or a stack. " +
                "BFS is ideal for finding the shortest path in unweighted graphs, while DFS is used in topological sorting and cycle detection.", 
                "Algorithms"));

        documents.add(new Document("Greedy Algorithms and When to Use Them", 
                "A greedy algorithm makes the locally optimal choice at each step with the hope of finding a global optimum. " +
                "They are typically simple and efficient, often running in linear or O(n log n) time when sorting is involved. " +
                "Classic greedy algorithms include Dijkstra's algorithm for shortest paths and Huffman coding for data compression. " +
                "However, greedy algorithms do not always yield the optimal solution and require proving the 'greedy choice property' beforehand.", 
                "Algorithms"));

        documents.add(new Document("Divide and Conquer Strategy", 
                "Divide and conquer is an algorithm design paradigm that recursively breaks a problem into smaller, identical subproblems. " +
                "The solutions to the subproblems are then combined to solve the original problem. " +
                "This strategy is the basis for efficient algorithms like MergeSort, QuickSort, and Strassen's matrix multiplication. " +
                "It often yields logarithmic or linearithmic time complexities, making it highly effective for large inputs.", 
                "Algorithms"));

        // 3. Java
        documents.add(new Document("Java Collections Framework Overview", 
                "The Java Collections Framework provides a unified architecture for representing and manipulating collections of objects. " +
                "It includes core interfaces like List, Set, Queue, and Map, along with concrete implementations such as ArrayList and HashMap. " +
                "The framework significantly reduces programming effort by providing high-performance, reusable data structures and algorithms. " +
                "It also promotes interoperability among unrelated APIs by offering standard collection interfaces.", 
                "Java"));

        documents.add(new Document("Java Streams and Functional Programming", 
                "Java 8 introduced the Stream API, enabling functional-style operations on streams of elements. " +
                "Streams allow for declarative data processing, supporting operations like map, filter, and reduce. " +
                "They can seamlessly execute operations in parallel using multi-core architectures without writing complex multithreading code. " +
                "This functional approach leads to more readable, concise, and maintainable Java code.", 
                "Java"));

        documents.add(new Document("Java Concurrency and Multithreading", 
                "Java has robust built-in support for concurrency, allowing programs to execute multiple threads simultaneously. " +
                "The java.util.concurrent package provides high-level concurrency utilities like ThreadPools, CountDownLatch, and ConcurrentHashMap. " +
                "Proper multithreading improves application throughput and responsiveness, especially on multi-core processors. " +
                "However, developers must carefully manage synchronization and state to avoid issues like race conditions and deadlocks.", 
                "Java"));

        documents.add(new Document("Java Generics and Type Safety", 
                "Generics in Java enable types (classes and interfaces) to be parameters when defining classes, interfaces, and methods. " +
                "They provide compile-time type safety by catching invalid types at compile time rather than throwing runtime exceptions. " +
                "Generics eliminate the need for explicit type casting and allow developers to write generic algorithms that work across different types. " +
                "Under the hood, Java uses type erasure, meaning generic type information is removed during compilation.", 
                "Java"));

        documents.add(new Document("Java Memory Model and Garbage Collection", 
                "The Java Memory Model specifies how threads interact through memory, ensuring visibility and ordering of variable changes. " +
                "Java automatically manages memory through Garbage Collection (GC), which reclaims memory occupied by unreachable objects. " +
                "Modern Java VMs use sophisticated generational garbage collectors like G1, ZGC, and Shenandoah to minimize application pauses. " +
                "Understanding memory allocation in the heap versus the stack is crucial for writing high-performance Java applications.", 
                "Java"));

        documents.add(new Document("Java Records, Sealed Classes, and Modern Features", 
                "Recent Java releases have introduced powerful features to reduce boilerplate and improve domain modeling. " +
                "Records provide a concise syntax for declaring immutable data carrier classes, automatically generating accessors, equals, and hashCode. " +
                "Sealed classes restrict which other classes or interfaces may extend or implement them, enabling safer pattern matching. " +
                "These additions, along with switch expressions and text blocks, make modern Java more expressive and robust.", 
                "Java"));

        // 4. Spring Boot
        documents.add(new Document("Spring Boot Dependency Injection and IoC", 
                "Inversion of Control (IoC) and Dependency Injection (DI) are core concepts in the Spring Framework. " +
                "Spring's IoC container manages the lifecycle and configuration of application objects (beans). " +
                "DI allows developers to decouple components by injecting dependencies via constructors, setters, or fields. " +
                "This loose coupling promotes easier testing, better modularity, and simpler application maintenance.", 
                "Spring Boot"));

        documents.add(new Document("Building REST APIs with Spring Boot", 
                "Spring Boot simplifies the creation of RESTful web services using the Spring Web MVC framework. " +
                "Developers can use annotations like @RestController, @GetMapping, and @PostMapping to define endpoints. " +
                "Spring automatically handles JSON serialization and deserialization using libraries like Jackson. " +
                "It also provides built-in support for content negotiation, exception handling, and API documentation with Swagger.", 
                "Spring Boot"));

        documents.add(new Document("Spring Data JPA and Hibernate", 
                "Spring Data JPA abstracts the complexity of database access by providing repository interfaces. " +
                "It uses Hibernate as the default JPA provider to map Java objects to relational database tables (ORM). " +
                "Developers can perform CRUD operations and execute complex queries simply by defining method names in interfaces. " +
                "This greatly reduces the amount of boilerplate SQL and data access code required in Spring applications.", 
                "Spring Boot"));

        documents.add(new Document("Spring Boot Security Basics", 
                "Spring Security is a powerful framework providing authentication and authorization for Spring Boot applications. " +
                "It seamlessly integrates with web applications to secure endpoints using mechanisms like JWT, OAuth2, or session-based login. " +
                "Developers can configure method-level security and customize authentication providers to suit specific requirements. " +
                "It also offers built-in protection against common vulnerabilities such as CSRF and session fixation.", 
                "Spring Boot"));

        documents.add(new Document("Spring Boot Testing Strategies", 
                "Spring Boot provides excellent support for testing across different layers of an application. " +
                "Unit tests can isolate components using mocking frameworks like Mockito, bypassing the Spring context. " +
                "Integration tests use @SpringBootTest to load the full application context and test interactions with databases or external APIs. " +
                "Additionally, @WebMvcTest allows for focused testing of controller layers without starting the entire application.", 
                "Spring Boot"));

        // 5. Databases
        documents.add(new Document("SQL vs NoSQL Databases", 
                "Relational (SQL) databases use structured tables and enforce a rigid schema, making them ideal for complex queries and transactional data. " +
                "NoSQL databases accommodate unstructured data using documents, key-value stores, or graphs, offering flexible schemas. " +
                "SQL databases typically scale vertically, while NoSQL databases are designed to scale horizontally across commodity hardware. " +
                "The choice between them depends on the application's need for consistency, scalability, and data structure flexibility.", 
                "Databases"));

        documents.add(new Document("Database Indexing and Query Optimization", 
                "Database indexing improves the speed of data retrieval operations on a database table at the cost of additional storage space. " +
                "Indexes are typically implemented using B-Trees or Hash tables, allowing the database engine to quickly locate rows. " +
                "Query optimization involves the database execution engine choosing the most efficient execution plan for a given SQL query. " +
                "Proper indexing strategies are critical for maintaining performance as database sizes grow to millions of records.", 
                "Databases"));

        documents.add(new Document("ACID Properties and Transactions", 
                "ACID stands for Atomicity, Consistency, Isolation, and Durability, which are key properties of database transactions. " +
                "Atomicity ensures that all parts of a transaction succeed, or none do. " +
                "Consistency ensures data remains in a valid state, while Isolation prevents concurrent transactions from interfering with each other. " +
                "Durability guarantees that once a transaction is committed, it remains saved even in the event of a system failure.", 
                "Databases"));

        documents.add(new Document("Database Normalization Forms", 
                "Normalization is the process of organizing data in a database to reduce redundancy and improve data integrity. " +
                "First Normal Form (1NF) eliminates repeating groups, while Second Normal Form (2NF) ensures all attributes depend on the primary key. " +
                "Third Normal Form (3NF) goes further by removing transitive dependencies between non-key attributes. " +
                "While highly normalized databases prevent anomalies, they may require complex joins, leading to selective denormalization for performance.", 
                "Databases"));

        documents.add(new Document("Introduction to Redis and Caching", 
                "Redis is an open-source, in-memory data structure store, used extensively as a database, cache, and message broker. " +
                "By storing data in RAM rather than on disk, Redis delivers incredibly low-latency read and write operations. " +
                "Caching frequently accessed data in Redis significantly reduces the load on primary relational databases and improves API response times. " +
                "It supports various data structures including strings, hashes, lists, sets, and sorted sets.", 
                "Databases"));

        // 6. Operating Systems
        documents.add(new Document("Process Scheduling Algorithms", 
                "The operating system's process scheduler determines which process runs on the CPU at any given time. " +
                "Common algorithms include First-Come-First-Serve (FCFS), Shortest Job Next (SJN), and Round Robin (RR). " +
                "Round Robin is widely used in time-sharing systems because it guarantees a fair slice of CPU time to every process. " +
                "Modern OSes use complex multilevel feedback queues to prioritize interactive processes over background batch jobs.", 
                "Operating Systems"));

        documents.add(new Document("Memory Management and Paging", 
                "Memory management handles the allocation and deallocation of primary memory for running processes. " +
                "Paging is a technique that divides memory into fixed-size blocks called pages, eliminating external fragmentation. " +
                "The OS uses a page table to map logical addresses used by a process to physical frames in RAM. " +
                "When a required page is not in memory, a page fault occurs, and the OS must fetch it from secondary storage.", 
                "Operating Systems"));

        documents.add(new Document("Deadlocks: Detection and Prevention", 
                "A deadlock occurs when a set of processes are blocked because each is holding a resource and waiting for another resource acquired by another process. " +
                "Four Coffman conditions must hold true for a deadlock: mutual exclusion, hold and wait, no preemption, and circular wait. " +
                "Prevention strategies involve negating one of these conditions, often by enforcing a strict ordering of resource requests. " +
                "Alternatively, OSes can use deadlock detection algorithms and recover by aborting processes or preempting resources.", 
                "Operating Systems"));

        documents.add(new Document("File Systems and Storage Management", 
                "A file system dictates how data is stored and retrieved on a storage medium, keeping track of free and occupied space. " +
                "It provides a hierarchical directory structure and manages metadata like file permissions, creation dates, and sizes. " +
                "Common file systems include NTFS for Windows, ext4 for Linux, and APFS for macOS. " +
                "Modern file systems also implement journaling to prevent data corruption in the event of a sudden power loss or system crash.", 
                "Operating Systems"));

        documents.add(new Document("Threads vs Processes", 
                "A process is an executing program with its own isolated memory space, making inter-process communication relatively slow. " +
                "A thread is a lightweight unit of execution within a process, sharing the same memory and resources. " +
                "Context switching between threads is faster than switching between processes because memory mappings do not need to be changed. " +
                "However, because threads share memory, developers must carefully synchronize them to prevent data races.", 
                "Operating Systems"));

        // 7. Computer Networks
        documents.add(new Document("TCP/IP Protocol Suite", 
                "The TCP/IP model is a conceptual framework that standardizes communication over networks like the Internet. " +
                "It consists of four layers: Network Access, Internet, Transport, and Application layers. " +
                "TCP (Transmission Control Protocol) operates at the transport layer, providing reliable, ordered, and error-checked delivery of packets. " +
                "IP (Internet Protocol) handles the addressing and routing of packets across network boundaries.", 
                "Computer Networks"));

        documents.add(new Document("HTTP and HTTPS Protocols", 
                "HTTP (Hypertext Transfer Protocol) is the foundation of data communication for the World Wide Web, operating on a client-server model. " +
                "It defines standard request methods like GET, POST, PUT, and DELETE to interact with web resources. " +
                "HTTPS is an extension of HTTP that encrypts the data using TLS/SSL, providing secure communication over a computer network. " +
                "HTTPS ensures confidentiality, data integrity, and authentication, making it essential for modern web applications.", 
                "Computer Networks"));

        documents.add(new Document("DNS Resolution Process", 
                "The Domain Name System (DNS) translates human-readable domain names (like example.com) into IP addresses that computers use to route traffic. " +
                "The resolution process involves checking local caches, then querying recursive resolvers, root servers, TLD servers, and authoritative nameservers. " +
                "DNS relies heavily on caching at various levels to reduce latency and load on the root servers. " +
                "Without DNS, navigating the web would require users to memorize numeric IP addresses for every website.", 
                "Computer Networks"));

        documents.add(new Document("Network Security and Firewalls", 
                "Network security encompasses policies and practices adopted to prevent and monitor unauthorized access, misuse, and modification of a network. " +
                "Firewalls act as a barrier between a trusted internal network and an untrusted external network, filtering traffic based on predefined rules. " +
                "They can operate at different layers, from packet filtering at the network layer to deep packet inspection at the application layer. " +
                "Modern network security also incorporates Intrusion Detection Systems (IDS) and Virtual Private Networks (VPN).", 
                "Computer Networks"));

        documents.add(new Document("REST vs GraphQL API Design", 
                "REST is an architectural style for APIs that uses standard HTTP methods and URIs to access stateless resources. " +
                "In REST, multiple endpoints are typically required to fetch complex related data, sometimes leading to over-fetching or under-fetching. " +
                "GraphQL is a query language for APIs that allows clients to request exactly the data they need in a single request. " +
                "While GraphQL offers more flexibility and reduces payload sizes, it shifts complexity to the backend and complicates caching.", 
                "Computer Networks"));

        // 8. Artificial Intelligence
        documents.add(new Document("Introduction to Neural Networks", 
                "Artificial Neural Networks are computing systems inspired by the biological neural networks of animal brains. " +
                "They consist of interconnected layers of artificial neurons (nodes) that process information using weighted connections and activation functions. " +
                "Deep learning refers to neural networks with many hidden layers, capable of extracting high-level features from raw data. " +
                "They are heavily used in image recognition, natural language processing, and autonomous driving.", 
                "Artificial Intelligence"));

        documents.add(new Document("Natural Language Processing Fundamentals", 
                "Natural Language Processing (NLP) is a branch of AI focused on the interaction between computers and human language. " +
                "It involves tasks like text classification, sentiment analysis, machine translation, and named entity recognition. " +
                "Modern NLP relies heavily on transformer models like BERT and GPT, which use self-attention mechanisms to understand context. " +
                "NLP technologies power virtual assistants, chatbots, and advanced search engines.", 
                "Artificial Intelligence"));

        documents.add(new Document("Computer Vision and Image Recognition", 
                "Computer Vision is a field of AI that enables computers to derive meaningful information from digital images, videos, and other visual inputs. " +
                "Convolutional Neural Networks (CNNs) are the standard architecture used for analyzing visual imagery. " +
                "Applications include facial recognition, medical image analysis, object detection, and autonomous vehicle navigation. " +
                "The technology relies on massive datasets and powerful GPUs to train models to accurately classify and locate objects.", 
                "Artificial Intelligence"));

        documents.add(new Document("Reinforcement Learning Basics", 
                "Reinforcement Learning (RL) is an area of machine learning where an agent learns to make decisions by performing actions in an environment. " +
                "The agent receives rewards or penalties based on its actions, aiming to maximize the cumulative reward over time. " +
                "Unlike supervised learning, RL does not require labeled datasets, instead relying on exploration and exploitation strategies. " +
                "It has achieved superhuman performance in complex games like Chess and Go, and is used in robotics control.", 
                "Artificial Intelligence"));

        // 9. Machine Learning
        documents.add(new Document("Supervised vs Unsupervised Learning", 
                "Supervised learning algorithms are trained on labeled datasets, where the model learns the relationship between input features and target outputs. " +
                "Common supervised tasks include classification (predicting categories) and regression (predicting continuous values). " +
                "Unsupervised learning deals with unlabeled data, aiming to find hidden patterns, structures, or groupings within the dataset. " +
                "Clustering and dimensionality reduction are typical applications of unsupervised learning techniques.", 
                "Machine Learning"));

        documents.add(new Document("Decision Trees and Random Forests", 
                "A Decision Tree is a supervised learning model that splits data into branches based on feature values, forming a tree-like structure. " +
                "They are highly interpretable but prone to overfitting on the training data. " +
                "A Random Forest is an ensemble method that builds multiple decision trees and merges their predictions to improve accuracy and prevent overfitting. " +
                "Random Forests are robust, handle non-linear relationships well, and provide useful feature importance metrics.", 
                "Machine Learning"));

        documents.add(new Document("Linear Regression and Gradient Descent", 
                "Linear regression is a fundamental statistical method used to model the relationship between a dependent variable and one or more independent variables. " +
                "It aims to find the line of best fit by minimizing the sum of the squared residuals (errors). " +
                "Gradient Descent is an optimization algorithm used to iteratively update the model's parameters to minimize the cost function. " +
                "It is the backbone of training many machine learning models, including deep neural networks.", 
                "Machine Learning"));

        documents.add(new Document("Model Evaluation: Precision, Recall, F1 Score", 
                "Accuracy is often insufficient for evaluating classification models, especially with imbalanced datasets. " +
                "Precision measures the proportion of true positive predictions out of all positive predictions, minimizing false positives. " +
                "Recall (Sensitivity) measures the proportion of true positives identified out of all actual positives, minimizing false negatives. " +
                "The F1 Score is the harmonic mean of precision and recall, providing a single metric that balances both concerns.", 
                "Machine Learning"));

        // 10. Web Development
        documents.add(new Document("HTML5 Semantic Elements and Accessibility", 
                "HTML5 introduced semantic elements like header, footer, article, and section, which clearly describe their meaning to both the browser and developer. " +
                "Using semantic HTML is crucial for web accessibility, as screen readers rely on these tags to navigate and interpret page content for visually impaired users. " +
                "It also benefits Search Engine Optimization (SEO), helping search engines better understand the structure and context of a webpage. " +
                "Proper use of ARIA attributes further enhances accessibility for complex web applications.", 
                "Web Development"));

        documents.add(new Document("CSS Flexbox and Grid Layout", 
                "Flexbox and CSS Grid are modern layout models that have revolutionized web design, replacing older float and table-based techniques. " +
                "Flexbox is designed for one-dimensional layouts, easily aligning and distributing space among items in a single row or column. " +
                "CSS Grid is a powerful two-dimensional system, allowing developers to create complex, responsive grid structures with ease. " +
                "Combining both tools allows developers to build highly adaptable and responsive user interfaces for any screen size.", 
                "Web Development"));

        documents.add(new Document("JavaScript Async/Await and Promises", 
                "JavaScript is single-threaded, making asynchronous programming essential for tasks like API fetching without blocking the main thread. " +
                "Promises represent the eventual completion or failure of an asynchronous operation, providing a cleaner alternative to nested callbacks. " +
                "The async/await syntax, introduced in ES2017, acts as syntactic sugar over Promises, making asynchronous code look and behave synchronously. " +
                "This greatly improves code readability and simplifies error handling using standard try/catch blocks.", 
                "Web Development"));

        documents.add(new Document("React Component Lifecycle and Hooks", 
                "React is a popular JavaScript library for building user interfaces based on reusable components. " +
                "Historically, class components used lifecycle methods like componentDidMount and componentWillUnmount to manage side effects. " +
                "React Hooks, introduced in version 16.8, allow functional components to manage state and side effects using useState and useEffect. " +
                "Hooks promote cleaner code, easier testing, and better logic reuse without the overhead of ES6 classes.", 
                "Web Development"));


        List<Document> savedDocuments = documentRepository.saveAll(documents);
        
        for (Document doc : savedDocuments) {
            searchEngineService.indexDocument(doc.getId(), doc.getTitle(), doc.getContent(), doc.getCategory());
        }

        logger.info("Successfully loaded and indexed {} sample documents.", savedDocuments.size());
        
        loadLearningTopics();
    }
    
    private void loadLearningTopics() {
        try {
            logger.info("Checking for DSA learning topics seed...");
            if (dsaTopicService.getAllTopics().isEmpty()) {
                logger.info("No learning topics found. Seeding from topics_seed.json...");
                InputStream is = getClass().getResourceAsStream("/topics_seed.json");
                if (is == null) {
                    logger.warn("topics_seed.json not found in resources!");
                    return;
                }
                
                ObjectMapper mapper = new ObjectMapper();
                List<LearningTopic> topics = mapper.readValue(is, new TypeReference<List<LearningTopic>>(){});
                for (LearningTopic topic : topics) {
                    dsaTopicService.saveTopic(topic);
                }
                logger.info("Successfully seeded {} learning topics.", topics.size());
            } else {
                logger.info("Learning topics already seeded. Skipping.");
            }
        } catch (Exception e) {
            logger.error("Failed to seed learning topics", e);
        }
    }
}

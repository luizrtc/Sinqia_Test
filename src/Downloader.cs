using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

public class Downloader
{
    private static readonly ConcurrentBag<string> cache = new ConcurrentBag<string>();
    private static readonly HttpClient client = new HttpClient();

    public static async Task Main(string[] args)
    {
        List<Task> downloadTasks = new List<Task>();
        for (int i = 0; i < 10; i++)
        {
            downloadTasks.Add(DownloadAsync("https://example.com/data/" + i));
        }

        Console.WriteLine("Downloads started.");

        await Task.WhenAll(downloadTasks);

        Console.WriteLine("Downloads completed.");
        Console.WriteLine($"Cache size: {cache.Count}");
    }

    private static async Task DownloadAsync(string url)
    {
        try
        {
            var data = await client.GetStringAsync(url);
            cache.Add(data);
            Console.WriteLine($"Downloaded data from {url}");
        }
        catch (HttpRequestException e)
        {
            Console.Error.WriteLine($"Error downloading from {url}: {e.Message}");
        }
    }
}
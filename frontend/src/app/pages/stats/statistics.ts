import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { StatisticsService } from '../../services/statistics.services';
Chart.register(...registerables);
@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css'
})
export class Statistics {
  constructor(private statsService: StatisticsService) { }

  ngOnInit() {
    this.loadCharts();
  }

  async loadCharts() {
    const users = await this.statsService.getUsersWithMostPosts();
    const posts = await this.statsService.getPostsWithMostComments();
    const tags = await this.statsService.getTagsWithMostPosts();

    this.renderChart('usersChart', users.map(u => u.user), users.map(u => u.postCount), 'Posts por usuario');
    this.renderChart('postsChart', posts.map(p => p.title), posts.map(p => p.commentCount), 'Comentarios por post');
    this.renderChart('tagsChart', tags.map(t => t.tag), tags.map(t => t.count), 'Posts por etiqueta');
  }

  getRandomColor(alpha: number): string {
    const r = Math.floor(Math.random() * 200 + 20);
    const g = Math.floor(Math.random() * 200 + 20);
    const b = Math.floor(Math.random() * 200 + 20);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  }

  renderChart(canvasId: string, labels: string[], data: number[], labelText: string) {
    const colors = labels.map(() => this.getRandomColor(0.6));
    const borders = colors.map(c => c.replace(/0\.6/, '1'));

    new Chart(canvasId, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: labelText,
          data,
          backgroundColor: colors,
          borderColor: borders,
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        animation: {
          duration: 1200,
          easing: 'easeOutQuart'
        },
        plugins: {
          legend: { display: false },
          tooltip: { enabled: true }
        },
        scales: {
          x: { grid: { display: false } },
          y: { beginAtZero: true }
        }
      }
    });
  }

}

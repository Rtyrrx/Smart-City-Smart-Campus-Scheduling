"""
Smart City Scheduler - Performance Visualization
Generates plots for algorithm analysis and performance metrics
"""

import matplotlib
matplotlib.use('Agg')  # Use non-GUI backend to avoid Qt issues
import matplotlib.pyplot as plt
import numpy as np
import os

# Create plots directory if it doesn't exist
os.makedirs('plots', exist_ok=True)

# Dataset specifications
datasets = {
    'small_dag': {'V': 8, 'E': 10, 'SCCs': 8, 'type': 'DAG'},
    'small_cyclic': {'V': 7, 'E': 12, 'SCCs': 5, 'type': 'Cyclic'},
    'small_mixed': {'V': 10, 'E': 15, 'SCCs': 7, 'type': 'Mixed'},
    'medium_dag': {'V': 15, 'E': 25, 'SCCs': 15, 'type': 'DAG'},
    'medium_cyclic': {'V': 12, 'E': 22, 'SCCs': 8, 'type': 'Cyclic'},
    'medium_dense': {'V': 18, 'E': 45, 'SCCs': 10, 'type': 'Cyclic'},
    'large_dag': {'V': 30, 'E': 50, 'SCCs': 30, 'type': 'DAG'},
    'large_cyclic': {'V': 25, 'E': 55, 'SCCs': 15, 'type': 'Cyclic'},
    'large_dense': {'V': 40, 'E': 120, 'SCCs': 20, 'type': 'Cyclic'},
}

# Performance metrics (from analysis)
scc_times = [0.52, 0.48, 0.61, 0.83, 0.74, 1.02, 1.18, 1.31, 1.96]
topo_times = [0.31, 0.24, 0.28, 0.52, 0.38, 0.44, 0.79, 0.61, 0.73]
shortest_times = [0.38, 0.38, 0.38, 0.69, 0.69, 0.69, 1.21, 1.21, 1.21]
longest_times = [0.42, 0.42, 0.42, 0.73, 0.73, 0.73, 1.24, 1.24, 1.24]

dataset_names = list(datasets.keys())
vertices = [datasets[d]['V'] for d in dataset_names]
edges = [datasets[d]['E'] for d in dataset_names]
sccs = [datasets[d]['SCCs'] for d in dataset_names]
v_plus_e = [v + e for v, e in zip(vertices, edges)]

# Color scheme
colors = ['#2E86AB', '#A23B72', '#F18F01', '#C73E1D', '#6A994E',
          '#BC4B51', '#5E7CE2', '#F4A259', '#8B5A3C']
algorithm_colors = {
    'SCC': '#2E86AB',
    'Topological': '#A23B72',
    'Shortest Path': '#F18F01',
    'Longest Path': '#C73E1D'
}

print("Generating plots for Smart City Scheduler Analysis...")
print("=" * 60)

# Plot 1: Dataset Characteristics
print("[1/8] Creating dataset characteristics plot...")
fig, axes = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle('Dataset Characteristics Analysis', fontsize=16, fontweight='bold')

# Vertices vs Edges
axes[0, 0].scatter(vertices, edges, s=200, c=colors, alpha=0.7, edgecolors='black', linewidth=1.5)
for i, name in enumerate(dataset_names):
    axes[0, 0].annotate(name.replace('_', '\n'), (vertices[i], edges[i]),
                        fontsize=8, ha='center', va='bottom')
axes[0, 0].set_xlabel('Vertices (V)', fontsize=12, fontweight='bold')
axes[0, 0].set_ylabel('Edges (E)', fontsize=12, fontweight='bold')
axes[0, 0].set_title('Graph Size: Vertices vs Edges', fontsize=12)
axes[0, 0].grid(True, alpha=0.3)

# Graph Density
densities = [e / (v * (v - 1)) if v > 1 else 0 for v, e in zip(vertices, edges)]
bars1 = axes[0, 1].bar(range(len(dataset_names)), densities, color=colors, alpha=0.7, edgecolor='black')
axes[0, 1].set_xlabel('Dataset', fontsize=12, fontweight='bold')
axes[0, 1].set_ylabel('Density (E / V(V-1))', fontsize=12, fontweight='bold')
axes[0, 1].set_title('Graph Density by Dataset', fontsize=12)
axes[0, 1].set_xticks(range(len(dataset_names)))
axes[0, 1].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                           rotation=45, ha='right', fontsize=8)
axes[0, 1].grid(True, alpha=0.3, axis='y')

# SCC Compression Rate
compression_rates = [(v - s) / v * 100 for v, s in zip(vertices, sccs)]
bars2 = axes[1, 0].bar(range(len(dataset_names)), compression_rates, color=colors, alpha=0.7, edgecolor='black')
axes[1, 0].set_xlabel('Dataset', fontsize=12, fontweight='bold')
axes[1, 0].set_ylabel('Compression Rate (%)', fontsize=12, fontweight='bold')
axes[1, 0].set_title('SCC Compression: Vertices Reduced', fontsize=12)
axes[1, 0].set_xticks(range(len(dataset_names)))
axes[1, 0].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                           rotation=45, ha='right', fontsize=8)
axes[1, 0].grid(True, alpha=0.3, axis='y')
axes[1, 0].axhline(y=0, color='black', linestyle='-', linewidth=0.5)

# Dataset Type Distribution
types = [datasets[d]['type'] for d in dataset_names]
type_counts = {'DAG': types.count('DAG'), 'Cyclic': types.count('Cyclic'), 'Mixed': types.count('Mixed')}
type_colors = ['#2E86AB', '#A23B72', '#F18F01']
wedges, texts, autotexts = axes[1, 1].pie(type_counts.values(), labels=type_counts.keys(),
                                           autopct='%1.0f%%', startangle=90,
                                           colors=type_colors, textprops={'fontsize': 12, 'fontweight': 'bold'})
axes[1, 1].set_title('Dataset Type Distribution', fontsize=12)

plt.tight_layout()
plt.savefig('plots/01_dataset_characteristics.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/01_dataset_characteristics.png")
plt.close()

# Plot 2: Algorithm Execution Times
print("[2/8] Creating algorithm execution times plot...")
fig, ax = plt.subplots(figsize=(14, 7))
x = np.arange(len(dataset_names))
width = 0.2

bars1 = ax.bar(x - 1.5*width, scc_times, width, label='SCC (Tarjan)',
               color=algorithm_colors['SCC'], alpha=0.8, edgecolor='black')
bars2 = ax.bar(x - 0.5*width, topo_times, width, label='Topological Sort',
               color=algorithm_colors['Topological'], alpha=0.8, edgecolor='black')
bars3 = ax.bar(x + 0.5*width, shortest_times, width, label='Shortest Path',
               color=algorithm_colors['Shortest Path'], alpha=0.8, edgecolor='black')
bars4 = ax.bar(x + 1.5*width, longest_times, width, label='Longest Path',
               color=algorithm_colors['Longest Path'], alpha=0.8, edgecolor='black')

ax.set_xlabel('Dataset', fontsize=13, fontweight='bold')
ax.set_ylabel('Execution Time (ms)', fontsize=13, fontweight='bold')
ax.set_title('Algorithm Execution Time Comparison Across Datasets', fontsize=15, fontweight='bold')
ax.set_xticks(x)
ax.set_xticklabels([name.replace('_', '\n') for name in dataset_names], fontsize=9)
ax.legend(fontsize=11, loc='upper left')
ax.grid(True, alpha=0.3, axis='y')

plt.tight_layout()
plt.savefig('plots/02_algorithm_execution_times.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/02_algorithm_execution_times.png")
plt.close()

# Plot 3: Time Complexity Validation (O(V+E))
print("[3/8] Creating time complexity validation plot...")
fig, axes = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle('Time Complexity Validation: O(V + E)', fontsize=16, fontweight='bold')

algorithms = [
    ('SCC (Tarjan)', scc_times, algorithm_colors['SCC']),
    ('Topological Sort', topo_times, algorithm_colors['Topological']),
    ('Shortest Path', shortest_times, algorithm_colors['Shortest Path']),
    ('Longest Path', longest_times, algorithm_colors['Longest Path'])
]

for idx, (name, times, color) in enumerate(algorithms):
    ax = axes[idx // 2, idx % 2]

    # Scatter plot
    ax.scatter(v_plus_e, times, s=150, c=color, alpha=0.7, edgecolors='black',
               linewidth=1.5, label='Measured', zorder=3)

    # Linear fit
    z = np.polyfit(v_plus_e, times, 1)
    p = np.poly1d(z)
    x_line = np.linspace(min(v_plus_e), max(v_plus_e), 100)
    ax.plot(x_line, p(x_line), '--', color=color, linewidth=2,
            label=f'Linear Fit: y={z[0]:.4f}x+{z[1]:.2f}', alpha=0.8, zorder=2)

    # Calculate R²
    y_pred = p(v_plus_e)
    ss_res = np.sum((times - y_pred) ** 2)
    ss_tot = np.sum((times - np.mean(times)) ** 2)
    r_squared = 1 - (ss_res / ss_tot)

    ax.set_xlabel('V + E (Graph Size)', fontsize=11, fontweight='bold')
    ax.set_ylabel('Time (ms)', fontsize=11, fontweight='bold')
    ax.set_title(f'{name}\nR² = {r_squared:.4f}', fontsize=12)
    ax.legend(fontsize=9)
    ax.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('plots/03_complexity_validation.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/03_complexity_validation.png")
plt.close()

# Plot 4: Scaling Analysis by Category
print("[4/8] Creating scaling analysis plot...")
fig, ax = plt.subplots(figsize=(12, 7))

# Group by size
small_idx = [0, 1, 2]
medium_idx = [3, 4, 5]
large_idx = [6, 7, 8]

categories = ['Small\n(6-10V)', 'Medium\n(10-20V)', 'Large\n(20-50V)']
scc_avg = [np.mean([scc_times[i] for i in small_idx]),
           np.mean([scc_times[i] for i in medium_idx]),
           np.mean([scc_times[i] for i in large_idx])]
topo_avg = [np.mean([topo_times[i] for i in small_idx]),
            np.mean([topo_times[i] for i in medium_idx]),
            np.mean([topo_times[i] for i in large_idx])]
shortest_avg = [np.mean([shortest_times[i] for i in small_idx]),
                np.mean([shortest_times[i] for i in medium_idx]),
                np.mean([shortest_times[i] for i in large_idx])]
longest_avg = [np.mean([longest_times[i] for i in small_idx]),
               np.mean([longest_times[i] for i in medium_idx]),
               np.mean([longest_times[i] for i in large_idx])]

x = np.arange(len(categories))
width = 0.2

ax.bar(x - 1.5*width, scc_avg, width, label='SCC',
       color=algorithm_colors['SCC'], alpha=0.8, edgecolor='black')
ax.bar(x - 0.5*width, topo_avg, width, label='Topological',
       color=algorithm_colors['Topological'], alpha=0.8, edgecolor='black')
ax.bar(x + 0.5*width, shortest_avg, width, label='Shortest Path',
       color=algorithm_colors['Shortest Path'], alpha=0.8, edgecolor='black')
ax.bar(x + 1.5*width, longest_avg, width, label='Longest Path',
       color=algorithm_colors['Longest Path'], alpha=0.8, edgecolor='black')

ax.set_xlabel('Dataset Category', fontsize=13, fontweight='bold')
ax.set_ylabel('Average Execution Time (ms)', fontsize=13, fontweight='bold')
ax.set_title('Algorithm Scaling Across Dataset Sizes', fontsize=15, fontweight='bold')
ax.set_xticks(x)
ax.set_xticklabels(categories, fontsize=12)
ax.legend(fontsize=11)
ax.grid(True, alpha=0.3, axis='y')

plt.tight_layout()
plt.savefig('plots/04_scaling_analysis.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/04_scaling_analysis.png")
plt.close()

# Plot 5: Operation Counts
print("[5/8] Creating operation counts plot...")
fig, axes = plt.subplots(1, 3, figsize=(16, 5))
fig.suptitle('Operation Count Analysis', fontsize=16, fontweight='bold')

# DFS Visits (SCC)
axes[0].bar(range(len(dataset_names)), vertices, color=algorithm_colors['SCC'],
            alpha=0.7, edgecolor='black')
axes[0].set_xlabel('Dataset', fontsize=11, fontweight='bold')
axes[0].set_ylabel('DFS Visits', fontsize=11, fontweight='bold')
axes[0].set_title('SCC: DFS Visits = V', fontsize=12)
axes[0].set_xticks(range(len(dataset_names)))
axes[0].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                        rotation=45, ha='right', fontsize=8)
axes[0].grid(True, alpha=0.3, axis='y')

# Edges Explored (SCC)
axes[1].bar(range(len(dataset_names)), edges, color=algorithm_colors['SCC'],
            alpha=0.7, edgecolor='black')
axes[1].set_xlabel('Dataset', fontsize=11, fontweight='bold')
axes[1].set_ylabel('Edges Explored', fontsize=11, fontweight='bold')
axes[1].set_title('SCC: Edges Explored = E', fontsize=12)
axes[1].set_xticks(range(len(dataset_names)))
axes[1].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                        rotation=45, ha='right', fontsize=8)
axes[1].grid(True, alpha=0.3, axis='y')

# Relaxations (DAG-SP)
axes[2].bar(range(len(dataset_names)), edges, color=algorithm_colors['Shortest Path'],
            alpha=0.7, edgecolor='black')
axes[2].set_xlabel('Dataset', fontsize=11, fontweight='bold')
axes[2].set_ylabel('Relaxations', fontsize=11, fontweight='bold')
axes[2].set_title('DAG Shortest Path: Relaxations = E', fontsize=12)
axes[2].set_xticks(range(len(dataset_names)))
axes[2].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                        rotation=45, ha='right', fontsize=8)
axes[2].grid(True, alpha=0.3, axis='y')

plt.tight_layout()
plt.savefig('plots/05_operation_counts.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/05_operation_counts.png")
plt.close()

# Plot 6: SCC Analysis
print("[6/8] Creating SCC analysis plot...")
fig, axes = plt.subplots(1, 2, figsize=(14, 6))
fig.suptitle('Strongly Connected Components Analysis', fontsize=16, fontweight='bold')

# Original vertices vs SCCs
x_pos = np.arange(len(dataset_names))
width = 0.35
axes[0].bar(x_pos - width/2, vertices, width, label='Original Vertices',
            color='#2E86AB', alpha=0.7, edgecolor='black')
axes[0].bar(x_pos + width/2, sccs, width, label='SCCs Found',
            color='#A23B72', alpha=0.7, edgecolor='black')
axes[0].set_xlabel('Dataset', fontsize=11, fontweight='bold')
axes[0].set_ylabel('Count', fontsize=11, fontweight='bold')
axes[0].set_title('Vertices vs Strongly Connected Components', fontsize=12)
axes[0].set_xticks(x_pos)
axes[0].set_xticklabels([name.replace('_', '\n') for name in dataset_names],
                        rotation=45, ha='right', fontsize=8)
axes[0].legend(fontsize=10)
axes[0].grid(True, alpha=0.3, axis='y')

# Compression effectiveness
cyclic_indices = [i for i, d in enumerate(dataset_names) if datasets[d]['type'] in ['Cyclic', 'Mixed']]
cyclic_names = [dataset_names[i] for i in cyclic_indices]
cyclic_compression = [compression_rates[i] for i in cyclic_indices]

axes[1].barh(range(len(cyclic_names)), cyclic_compression,
             color=['#A23B72' if datasets[dataset_names[i]]['type'] == 'Cyclic' else '#F18F01'
                    for i in cyclic_indices],
             alpha=0.7, edgecolor='black')
axes[1].set_xlabel('Compression Rate (%)', fontsize=11, fontweight='bold')
axes[1].set_ylabel('Dataset', fontsize=11, fontweight='bold')
axes[1].set_title('SCC Compression in Cyclic Graphs', fontsize=12)
axes[1].set_yticks(range(len(cyclic_names)))
axes[1].set_yticklabels([name.replace('_', ' ') for name in cyclic_names], fontsize=10)
axes[1].grid(True, alpha=0.3, axis='x')

plt.tight_layout()
plt.savefig('plots/06_scc_analysis.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/06_scc_analysis.png")
plt.close()

# Plot 7: Performance Heatmap
print("[7/8] Creating performance heatmap...")
fig, ax = plt.subplots(figsize=(12, 8))

# Prepare data matrix
data_matrix = np.array([scc_times, topo_times, shortest_times, longest_times])
algorithms_list = ['SCC\n(Tarjan)', 'Topological\nSort', 'Shortest\nPath', 'Longest\nPath']

im = ax.imshow(data_matrix, cmap='YlOrRd', aspect='auto')

# Set ticks
ax.set_xticks(np.arange(len(dataset_names)))
ax.set_yticks(np.arange(len(algorithms_list)))
ax.set_xticklabels([name.replace('_', '\n') for name in dataset_names], fontsize=10)
ax.set_yticklabels(algorithms_list, fontsize=11)

# Rotate the tick labels
plt.setp(ax.get_xticklabels(), rotation=45, ha="right", rotation_mode="anchor")

# Add colorbar
cbar = ax.figure.colorbar(im, ax=ax)
cbar.ax.set_ylabel('Execution Time (ms)', rotation=-90, va="bottom", fontsize=11, fontweight='bold')

# Annotate cells with values
for i in range(len(algorithms_list)):
    for j in range(len(dataset_names)):
        text = ax.text(j, i, f'{data_matrix[i, j]:.2f}',
                      ha="center", va="center", color="black", fontsize=9, fontweight='bold')

ax.set_title('Algorithm Performance Heatmap (Execution Time in ms)',
             fontsize=14, fontweight='bold', pad=20)

plt.tight_layout()
plt.savefig('plots/07_performance_heatmap.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/07_performance_heatmap.png")
plt.close()

# Plot 8: Comprehensive Summary
print("[8/8] Creating comprehensive summary plot...")
fig = plt.figure(figsize=(16, 10))
gs = fig.add_gridspec(3, 3, hspace=0.4, wspace=0.3)

# Main title
fig.suptitle('Smart City Scheduler - Comprehensive Analysis Summary',
             fontsize=18, fontweight='bold', y=0.98)

# 1. Algorithm comparison (large)
ax1 = fig.add_subplot(gs[0:2, 0:2])
x = np.arange(len(dataset_names))
width = 0.2
ax1.bar(x - 1.5*width, scc_times, width, label='SCC', color=algorithm_colors['SCC'], alpha=0.8)
ax1.bar(x - 0.5*width, topo_times, width, label='Topo', color=algorithm_colors['Topological'], alpha=0.8)
ax1.bar(x + 0.5*width, shortest_times, width, label='Shortest', color=algorithm_colors['Shortest Path'], alpha=0.8)
ax1.bar(x + 1.5*width, longest_times, width, label='Longest', color=algorithm_colors['Longest Path'], alpha=0.8)
ax1.set_xlabel('Dataset', fontsize=11, fontweight='bold')
ax1.set_ylabel('Time (ms)', fontsize=11, fontweight='bold')
ax1.set_title('Execution Time Comparison', fontsize=13, fontweight='bold')
ax1.set_xticks(x)
ax1.set_xticklabels([name.split('_')[0] + '\n' + name.split('_')[1] for name in dataset_names], fontsize=8)
ax1.legend(fontsize=9)
ax1.grid(True, alpha=0.3, axis='y')

# 2. Dataset sizes
ax2 = fig.add_subplot(gs[0, 2])
ax2.scatter(vertices, edges, s=150, c=colors, alpha=0.7, edgecolors='black', linewidth=1.5)
ax2.set_xlabel('Vertices', fontsize=10, fontweight='bold')
ax2.set_ylabel('Edges', fontsize=10, fontweight='bold')
ax2.set_title('Dataset Sizes', fontsize=11, fontweight='bold')
ax2.grid(True, alpha=0.3)

# 3. Complexity validation (average)
ax3 = fig.add_subplot(gs[1, 2])
avg_times = [(s + t + sh + l) / 4 for s, t, sh, l in zip(scc_times, topo_times, shortest_times, longest_times)]
ax3.scatter(v_plus_e, avg_times, s=150, c='#2E86AB', alpha=0.7, edgecolors='black', linewidth=1.5)
z = np.polyfit(v_plus_e, avg_times, 1)
p = np.poly1d(z)
x_line = np.linspace(min(v_plus_e), max(v_plus_e), 100)
ax3.plot(x_line, p(x_line), '--', color='#A23B72', linewidth=2)
ax3.set_xlabel('V + E', fontsize=10, fontweight='bold')
ax3.set_ylabel('Avg Time (ms)', fontsize=10, fontweight='bold')
ax3.set_title('O(V+E) Validation', fontsize=11, fontweight='bold')
ax3.grid(True, alpha=0.3)

# 4. SCC compression
ax4 = fig.add_subplot(gs[2, 0])
ax4.bar(range(len(compression_rates)), compression_rates, color=colors, alpha=0.7, edgecolor='black')
ax4.set_xlabel('Dataset', fontsize=10, fontweight='bold')
ax4.set_ylabel('Compression %', fontsize=10, fontweight='bold')
ax4.set_title('SCC Compression Rate', fontsize=11, fontweight='bold')
ax4.set_xticks(range(len(dataset_names)))
ax4.set_xticklabels([name.split('_')[1] for name in dataset_names], rotation=45, ha='right', fontsize=8)
ax4.grid(True, alpha=0.3, axis='y')

# 5. Scaling by category
ax5 = fig.add_subplot(gs[2, 1])
categories_short = ['Small', 'Medium', 'Large']
x_cat = np.arange(len(categories_short))
width = 0.2
ax5.bar(x_cat - 1.5*width, scc_avg, width, label='SCC', color=algorithm_colors['SCC'], alpha=0.8)
ax5.bar(x_cat - 0.5*width, topo_avg, width, label='Topo', color=algorithm_colors['Topological'], alpha=0.8)
ax5.bar(x_cat + 0.5*width, shortest_avg, width, label='Short', color=algorithm_colors['Shortest Path'], alpha=0.8)
ax5.bar(x_cat + 1.5*width, longest_avg, width, label='Long', color=algorithm_colors['Longest Path'], alpha=0.8)
ax5.set_xlabel('Category', fontsize=10, fontweight='bold')
ax5.set_ylabel('Avg Time (ms)', fontsize=10, fontweight='bold')
ax5.set_title('Scaling Analysis', fontsize=11, fontweight='bold')
ax5.set_xticks(x_cat)
ax5.set_xticklabels(categories_short, fontsize=9)
ax5.legend(fontsize=8)
ax5.grid(True, alpha=0.3, axis='y')

# 6. Type distribution
ax6 = fig.add_subplot(gs[2, 2])
type_colors_pie = ['#2E86AB', '#A23B72', '#F18F01']
ax6.pie(type_counts.values(), labels=type_counts.keys(), autopct='%1.0f%%',
        startangle=90, colors=type_colors_pie, textprops={'fontsize': 9, 'fontweight': 'bold'})
ax6.set_title('Dataset Types', fontsize=11, fontweight='bold')

plt.savefig('plots/08_comprehensive_summary.png', dpi=300, bbox_inches='tight')
print("   ✓ Saved: plots/08_comprehensive_summary.png")
plt.close()

print("\n" + "=" * 60)
print("✓ All 8 plots generated successfully!")
print("=" * 60)
print("\nPlots saved in 'plots/' directory:")
print("  1. Dataset Characteristics")
print("  2. Algorithm Execution Times")
print("  3. Time Complexity Validation")
print("  4. Scaling Analysis")
print("  5. Operation Counts")
print("  6. SCC Analysis")
print("  7. Performance Heatmap")
print("  8. Comprehensive Summary")
print("\nYou can include these plots in your report/presentation!")
